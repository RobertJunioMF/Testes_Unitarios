package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
	}

	@Test
	public void testeSomaDoisValores() {

		int a = 5;
		int b = 3;
		int resultado = calc.somar(a, b);

		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void testeDividirDoisValores() throws NaoPodeDividirPorZeroException {

		int a = 6;
		int b = 3;
		int resultado = calc.dividir(a, b);

		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void testeLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		
		int a = 10;
		int b = 0;
		calc.dividir(a, b);
	}
}
