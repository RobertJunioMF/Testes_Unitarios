package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Parameter //primeiro registo da matriz de parametros
	public List<Filme> filmes;
	@Parameter(value=1) //segundo registro da matriz de parametros
	public Double valorLocacao;
	@Parameter(value=2) //terceiro registro da matriz de parametros
	public String msg;
	private static Filme filme1 = FilmeBuilder.umFilme().agora();
	private static Filme filme2 = FilmeBuilder.umFilme().agora();
	private static Filme filme3 = FilmeBuilder.umFilme().agora();
	private static Filme filme4 = FilmeBuilder.umFilme().agora();
	private static Filme filme5 = FilmeBuilder.umFilme().agora();
	private static Filme filme6 = FilmeBuilder.umFilme().agora();
		
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2, filme3), 11.0, "Desconto 25% no filme 3"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "Desconto 50% no filme 4"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "Desconto 75% no filme 5"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "Desconto 100% no filme 6"},
		});
	}
	
	@Test
	public void testeCalculoValorConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
	
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		assertThat(resultado.getValor(), is(valorLocacao));
	}
}
