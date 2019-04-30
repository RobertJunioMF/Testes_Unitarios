package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTests {

	@Test
	public void exemplos() {
		
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals("Erro na linha 16 ", 1, 1); // mensagem para caso ocorra erro no teste
		Assert.assertEquals(0.5123, 0.512, 0.001);
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2);
		
		Assert.assertEquals("thanos", "thanos");
		Assert.assertNotEquals("thanos", "tony");
		Assert.assertTrue("thanos".equalsIgnoreCase("Thanos"));
		Assert.assertTrue("thanos".startsWith("th"));
		
		Usuario u1 = new Usuario("Usuário 1");
		Usuario u2 = new Usuario("Usuário 1");
		Assert.assertEquals(u1,  u2);
		Assert.assertSame(u2, u2); // comparar instancias
		Assert.assertNotSame(u1, u2); // comparar instancias
	}
}
