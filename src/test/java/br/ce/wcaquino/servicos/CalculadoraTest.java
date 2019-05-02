package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.DividirPorZeroException;

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
