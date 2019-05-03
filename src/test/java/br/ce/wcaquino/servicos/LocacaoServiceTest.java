package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.LocacaoBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	/*
	 * Declarar uma variavel estatica impede que o junit reinicie a variavel
	 */
	private LocacaoService service;
	private SPCService spc;
	private LocacaoDAO dao;
	private EmailService email;
		
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before //executa antes do teste
	public void setup() {
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		spc = Mockito.mock(SPCService.class);
		service.setSPCService(spc);
		email = Mockito.mock(EmailService.class);
		service.setEmailService(email);
	}
	
//	@BeforeClass
//	public static void beforeClass() {
//		System.out.println("Before class");
//	}
//	
//	@AfterClass
//	public static void afterClass() {
//		System.out.println("After class");
//	}
//	
	@Test
	public void testeLocacao() throws Exception {
		
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		Assert.assertEquals("Erro na linha 64 ", 4.0, locacao.getValor(), 0.01); //comentario para facilitar a localização do erro
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(4.0))); //verifique que
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.not(5.0)));
		assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(3)), is(false)); //aqui foi realizado o importe estatico
		
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeFilmeSemEstoque() throws Exception {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora());
		
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void testeUsuarioVazio() throws FilmeSemEstoqueException {

		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {

			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	@Test
	public void testeFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		service.alugarFilme(usuario, null);
		
	}
	
	@Test
	public void testeSemDevolucaoAosDomingos() throws FilmeSemEstoqueException, LocadoraException {
		//indica para o teste ser executado somente aos sábados
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY)); 
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		boolean valido = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.SUNDAY);
		Assert.assertFalse(valido);
	}
	
	@Test
	public void testeNaoAlugarParaNegativadoSPC() throws FilmeSemEstoqueException {
		
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
				
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário Negativado"));
		}
		
		Mockito.verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void testeEnvioEmailParaLocacoesAtrasadas() {

		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuário 2").agora();
		List<Locacao> locacoes = Arrays.asList(LocacaoBuilder.umLocacao().comUsuario(usuario)
				.comDataLocacao(DataUtils.obterDataComDiferencaDias(-4))
				.comDataRetorno(DataUtils.obterDataComDiferencaDias(-2)).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora());
				
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

		service.notificarAtrasos();

		Mockito.verify(email).notificarAtraso(usuario);
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verify(email, Mockito.times(1)).notificarAtraso(Mockito.any(Usuario.class)); //não importa o usuário, mas sim que tenha enviado pelo menos 1 email
	}

//	@Test
//	public void testeDescontoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
//		//25% desconto no terceiro filme
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 3, 4.0), new Filme("Filme 2", 3, 4.0), 
//				new Filme("Filme 3", 1, 4.0));
//	
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		//4+4+3
//		assertThat(resultado.getValor(), is(11.0));
//	}
//	
//	@Test
//	public void testeDescontoNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
//		//50% desconto no quarto filme
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 3, 4.0), new Filme("Filme 2", 3, 4.0), 
//				new Filme("Filme 3", 1, 4.0), new Filme("Filme 4", 1, 4.0));
//	
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		//4+4+3+2
//		assertThat(resultado.getValor(), is(13.0));
//	}
//	
//	@Test
//	public void testeDescontoNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
//		//75% desconto no quarto filme
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 3, 4.0), new Filme("Filme 2", 3, 4.0), 
//				new Filme("Filme 3", 1, 4.0), new Filme("Filme 4", 1, 4.0),
//				new Filme("Filme 5", 1, 4.0));
//	
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		//4+4+3+2+1
//		assertThat(resultado.getValor(), is(14.0));
//	}
//	
//	@Test
//	public void testeDescontoNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
//		//100% desconto no quarto filme
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 3, 4.0), new Filme("Filme 2", 3, 4.0), 
//				new Filme("Filme 3", 1, 4.0), new Filme("Filme 4", 1, 4.0),
//				new Filme("Filme 5", 1, 4.0), new Filme("Filme 6", 5, 4.0));
//	
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//		//4+4+3+2+1
//		assertThat(resultado.getValor(), is(14.0));
//	}
	
}
