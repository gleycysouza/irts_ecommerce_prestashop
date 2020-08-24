package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {
	
	ProdutoPage produtoPage;
	LoginPage loginpage;
	String nomeProduto_ProdutoPage;
	ModalProdutoPage modalProdutoPage;
	
	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is (8));
	}
	
	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		//int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		//System.out.println(produtosNoCarrinho);
		assertThat(homePage.obterQuantidadeProdutosNoCarrinho(), is (0));
	}

	
	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);
		
		//System.out.println(nomeProduto_HomePage);
		//System.out.println(precoProduto_HomePage);
		
		produtoPage = homePage.clicarProduto(indice);
		
		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();
		
		//System.out.println(nomeProduto_ProdutoPage);
		//System.out.println(precoProduto_ProdutoPage);
		
		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}
	
	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		//Clicar no botao sign in na homepage
		loginpage = homePage.clicarBotaoSignIn();
		
		//preencher usuario e senha
		loginpage.preencherEmail("gleycy@teste.com");
		loginpage.preencherPassword("gleycy");
		
		//clicar no botao sign in para logar
		loginpage.clicarOBotaoSignIn();
		
		//validar se o usuário está logado de fato
		assertThat(homePage.estaLogado("Gleycy Souza"), is(true));
		
		carregarPaginaInicial();
	}
	
	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {
		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;
		
		//Pre condicao
		//usuario logado
		if(!homePage.estaLogado("Gleycy Souza")) {
			testLoginComSucesso_UsuarioLogado();
		}
		
		// teste
		//selecionando produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();
		
		//selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());
		
		//alteracao do tamanho
		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());
		
		//selecionar cor
		produtoPage.selecionarCorPreta();
		
		//selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);
		
		//clicar no botao add to cart
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		
		//validacoes
		//assertThat(modalProdutoPage.obterMensagemProdutoAdicionado(), is("Product successfully added to your shopping cart"));
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado().endsWith("Product successfully added to your shopping cart"));
		
		assertThat(modalProdutoPage.obterTamanhoProduto(), is (tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is (corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is (Integer.toString(quantidadeProduto)));
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		
		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		
		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subtotalString);
		
		Double subtotalCalculado = quantidadeProduto * precoProduto;
		
		assertThat(subtotal, is (subtotalCalculado));
	}
	
	//Valores esperados
	
	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;
	
	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalProduto + esperado_shippingTotal;
	Double esperado_totalTaxIncTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;
	String esperado_nomeCliente = "Gleycy Souza";
	
	CarrinhoPage carrinhoPage;
	
	@Test
	public void IrParaCarrinho_InformacoesPersistidas() {
		//precondicoes
		//produto incluido na tela modalprodutopage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();
		
		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();
		
		// teste
		// validar todos os elementos da tela
		/*System.out.println("**** TELA DO CARRINHO *****");
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));
		
		System.out.println("**** TELA DE TOTAIS *****");
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
		*/
		//Asserções Hamcrest
		// retornodapaginaweb, retornoesperado
		assertThat(carrinhoPage.obter_nomeProduto(),is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()),is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()), is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()), is(esperado_subtotalProduto));
		
		
		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		//Asserções JUnit
		// retornoesperado, retornodapaginaweb
		//assertEquals(esperado_nomeProduto, carrinhoPage.obter_nomeProduto());
	}
	
	CheckoutPage checkoutPage; 
	
	@Test
	public void IrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		//pre condições
		
		//produto escolhido no carrinho de compras
		IrParaCarrinho_InformacoesPersistidas();
		
		//Teste
		
		//Clicar no botão
		checkoutPage = carrinhoPage.clicarBotaoProceedToChekout();
		
		//preencher informações na tela
		
		
		//validar informações
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));
		
		
		checkoutPage.clicarBotaoContinueAddress();
		
		
		assertThat(Funcoes.removeCifraoDevolveDouble((Funcoes.removeTexto(checkoutPage.obter_shippingValor()," tax excl."))), 
				is(esperado_shippingTotal));
		
		checkoutPage.clicarBotaoContinueShipping();
		
		// selecionar a opçãp pay bu check
		checkoutPage.selecionarRadioPayByCheck();
		
		// Validar valor do cheque (amount)
		assertThat(Funcoes.removeCifraoDevolveDouble(Funcoes.removeTexto(checkoutPage.obter_amountPayByCheck(), " (tax incl.)")),
				is(esperado_totalTaxIncTotal));
		
		//Clicar na opção I agree
		checkoutPage.selecionarcheckBoxIAgree();
		assertTrue(checkoutPage.estaSelecionadoCheckBoxIAgree());
	}
	
	PedidoPage pedidoPage;
	
	@Test
	public void finalizarPedido_PedidoFinalizadoComSucesso() {
		//precondicoes: checkout concluido
		
		IrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();
		
		//Teste
		//clicar botao para confirmar pedido
		pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();
		
		//Validar valores da tela
		//assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is("YOUR ORDER IS CONFIRMED"));
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		assertThat(pedidoPage.obter_email(), is("gleycy@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(), is (esperado_subtotalProduto));
		assertThat(pedidoPage.obter_totalTaxaIncl(), is (esperado_totalTaxIncTotal));
		assertThat(pedidoPage.obter_metodoPagamento(), is ("check"));
		
	}
	
}
