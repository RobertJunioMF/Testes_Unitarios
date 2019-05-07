package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.exceptions.DividirPorZeroException;

public class CalculadoraTest {
	
	private Calculadora calc;
	@Mock
	private Calculadora calcMock;
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setup() {
		calc = new Calculadora();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testeDiferencaMockSpy() {
		Mockito.when(calcMock.somar(1, 2)).thenReturn(8);
		//Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod(); //para o mock também executar o metodo
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(8);
		
		System.out.println("Mock: " + calcMock.somar(1, 5)); //quando nao sabe o que fazer volta o valor padrao
		System.out.println("Spy: " + calcSpy.somar(1, 5)); //quando nao sabe o que fazer ele executa o metodo
	}
	
	@Test
	public void testeSomaDoisValores() {

		int a = 5;
		int b = 3;
		int resultado = calc.somar(a, b);

		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void testeDividirDoisValores() throws DividirPorZeroException {

		int a = 6;
		int b = 3;
		int resultado = calc.dividir(a, b);

		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = DividirPorZeroException.class)
	public void testeLancarExcecaoAoDividirPorZero() throws DividirPorZeroException {
		
		int a = 10;
		int b = 0;
		calc.dividir(a, b);
	}
}
