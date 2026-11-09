import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.exception.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Generate {
    static ProjectAccessor pa;
    static IModel project;
    static BasicModelEditor bme;
    static ClassDiagramEditor cde;
    static SequenceDiagramEditor sde;
    static UseCaseModelEditor uce;

    static final String PRJ = "/home/maycon/Trabalho_Unidade02/Trabalho_Unidade02.asta";

    static void p(String s) { System.out.println("[gen] " + s); System.out.flush(); }

    static String title(int n) {
        String[] t = {
            "Saque em Caixa Eletrônico (Simples)",
            "Login com Alternativa de Falha",
            "Adicionando Item ao Carrinho (Criação de Objeto)",
            "Removendo Item do Carrinho (Destruição de Objeto)",
            "Compra Completa com Validação e Estoque",
            "Classe Abstrata e Herança",
            "Interfaces",
            "Padrão de Projeto - Strategy",
            "Padrão de Projeto - Observer",
            "Padrão de Projeto - Factory Method"
        };
        return String.format("Questao %02d - %s", n, t[n-1]);
    }

    static IPackage pkg(int n) throws Exception {
        return bme.createPackage(project, String.format("Q%02d", n));
    }

    static IClass cls(IPackage p, String name) throws Exception { return bme.createClass(p, name); }
    static IClass iface(IPackage p, String name) throws Exception { return bme.createInterface(p, name); }

    static IAttribute at(IClass c, String name, String type, String vis) throws Exception {
        IAttribute a = bme.createAttribute(c, name, type);
        a.setVisibility(vis);
        return a;
    }

    static IOperation op(IClass c, String name, String ret, String vis) throws Exception {
        IOperation o = bme.createOperation(c, name, ret);
        o.setVisibility(vis);
        return o;
    }

    static void par(IOperation o, String name, String type) throws Exception {
        bme.createParameter(o, name, type);
    }

    // ---------- sequence helpers ----------
    static INodePresentation lifeActor(IClass base, double x, double len) throws Exception {
        INodePresentation lp = sde.createLifeline("", x);
        ((ILifeline) lp.getModel()).setBase(base);
        lp.setProperty("lifeline_length", String.valueOf((int) len));
        return lp;
    }

    static INodePresentation life(IClass base, String name, double x, double len) throws Exception {
        INodePresentation lp = sde.createLifeline(name, x);
        ((ILifeline) lp.getModel()).setBase(base);
        lp.setProperty("lifeline_length", String.valueOf((int) len));
        String lbl = lp.getLabel();
        if (lbl != null) lp.setWidth(Math.max(120, 55 + 7.0 * lbl.length()));
        return lp;
    }

    static ILinkPresentation msg(String name, String args, INodePresentation a, INodePresentation b, double y) throws Exception {
        ILinkPresentation m = sde.createMessage(name, a, b, y);
        if (args != null && args.length() > 0) ((IMessage) m.getModel()).setArgument(args);
        return m;
    }

    static void shiftLabelX(ILinkPresentation m, double dx) throws Exception {
        double x = Double.parseDouble(m.getProperty("name.point.x"));
        m.setProperty("name.point.x", String.valueOf(x + dx));
    }

    static ILinkPresentation ret(String name, ILinkPresentation base) throws Exception {
        return sde.createReturnMessage(name, base);
    }

    // ---------- class helpers ----------
    static INodePresentation node(IClass c, double x, double y) throws Exception {
        INodePresentation n = cde.createNodePresentation(c, new Point2D.Double(x, y));
        return n;
    }

    static void normal(INodePresentation n) throws Exception {
        n.setProperty("notation_type", "normal");
    }

    static void hideFrame(IDiagram d) throws Exception {
        for (IPresentation pr : d.getPresentations()) {
            if ("Frame".equals(pr.getType())) pr.setProperty("visibility", "false");
        }
    }

    static void note(String text, double x, double y, IPresentation anchor) throws Exception {
        INodePresentation n = cde.createNote(text, new Point2D.Double(x, y));
        if (anchor != null) cde.createNoteAnchor(n, anchor);
    }

    static ILinkPresentation link(IElement rel, INodePresentation a, INodePresentation b) throws Exception {
        return cde.createLinkPresentation(rel, a, b);
    }

    // =========================================================
    public static void main(String[] args) throws Exception {
        pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create(PRJ);
        project = pa.getProject();
        bme = ModelEditorFactory.getBasicModelEditor();
        cde = pa.getDiagramEditorFactory().getClassDiagramEditor();
        sde = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        uce = pa.getModelEditorFactory().getUseCaseModelEditor();

        TransactionManager.beginTransaction();
        bme.setLanguageJava(project, true);

        seq01(); p("Q01 ok");
        seq02(); p("Q02 ok");
        seq03(); p("Q03 ok");
        seq04(); p("Q04 ok");
        seq05(); p("Q05 ok");
        cls06(); p("Q06 ok");
        cls07(); p("Q07 ok");
        cls08(); p("Q08 ok");
        cls09(); p("Q09 ok");
        cls10(); p("Q10 ok");

        TransactionManager.endTransaction();
        pa.save();
        p("SAVED " + PRJ);
        pa.close();
    }

    // =========================================================
    static void seq01() throws Exception {
        IPackage p = pkg(1);
        IClass cliente = uce.createActor(p, "Cliente");
        IClass caixa = cls(p, "CaixaEletronico");
        IClass banco = cls(p, "Banco");

        ISequenceDiagram d = sde.createSequenceDiagram(project, title(1));
        INodePresentation cl = lifeActor(cliente, 50, 750);
        INodePresentation cx = life(caixa, "", 350, 750);
        INodePresentation bc = life(banco, "", 700, 750);

        msg("inserirCartao", "cartao", cl, cx, 100);
        msg("solicitarSenha", "", cx, cl, 165);
        msg("informarSenha", "senha", cl, cx, 230);
        ILinkPresentation m4 = msg("validarSenha", "cartao, senha", cx, bc, 295);
        ret("senhaValida", m4);
        msg("solicitarValor", "", cx, cl, 385);
        msg("informarValor", "valor", cl, cx, 450);
        ILinkPresentation m7 = msg("processarSaque", "valor", cx, bc, 515);
        ret("autorizacaoSaque", m7);
        msg("entregarDinheiro", "valor", cx, cl, 605);
    }

    static void seq02() throws Exception {
        IPackage p = pkg(2);
        IClass visitante = uce.createActor(p, "Visitante");
        IClass tela = cls(p, "TelaDeLogin");
        IClass sis = cls(p, "SistemaDeAutenticacao");
        IClass pag = cls(p, "PaginaPrincipal");

        ISequenceDiagram d = sde.createSequenceDiagram(project, title(2));
        INodePresentation vp = lifeActor(visitante, 50, 820);
        INodePresentation tp = life(tela, "", 300, 820);
        INodePresentation sp = life(sis, "", 600, 820);
        INodePresentation pp = life(pag, "", 900, 820);

        msg("informarCredenciais", "email, senha", vp, tp, 100);
        msg("autenticar", "email, senha", tp, sp, 165);
        ILinkPresentation m3 = msg("verificarCredenciais", "", sp, sp, 230);
        shiftLabelX(m3, -170);

        ILinkPresentation m4 = msg("consultarResultado", "", tp, sp, 330);
        ILinkPresentation r4 = ret("credenciaisValidas", m4);
        ILinkPresentation m5 = msg("redirecionar", "", tp, pp, 420);
        ILinkPresentation r5 = ret("paginaCarregada", m5);

        ILinkPresentation m6 = msg("obterErro", "", tp, sp, 530);
        ILinkPresentation r6 = ret("credenciaisInvalidas", m6);
        ILinkPresentation m7 = msg("exibirMensagem", "\"Mensagem de Erro\"", tp, vp, 620);

        IPresentation[] t1 = new IPresentation[]{vp, tp, sp, pp, m4, r4, m5, r5};
        INodePresentation cf = sde.createCombinedFragment("alt", t1);
        ICombinedFragment frag = (ICombinedFragment) cf.getModel();
        frag.getInteractionOperands()[0].setGuard("credenciais válidas");
        sde.addInteractionOperand(cf, new IPresentation[]{m6, r6, m7}, "", "credenciais inválidas");
    }

    static void seq03() throws Exception {
        IPackage p = pkg(3);
        IClass consumidor = uce.createActor(p, "Consumidor");
        IClass pagProd = cls(p, "PaginaDeProduto");
        IClass carrinho = cls(p, "CarrinhoDeCompras");
        IClass itemCls = cls(p, "ItemDoCarrinho");

        ISequenceDiagram d = sde.createSequenceDiagram(project, title(3));
        INodePresentation cp = lifeActor(consumidor, 50, 760);
        INodePresentation pg = life(pagProd, "", 300, 760);
        INodePresentation cr = life(carrinho, "", 600, 760);
        INodePresentation it = life(itemCls, "item", 980, 760);

        msg("clicarAdicionar", "produto", cp, pg, 100);
        msg("adicionarProduto", "produto, quantidade", pg, cr, 165);
        ILinkPresentation cm = sde.createCreateMessage("criarItem", cr, it, 250);
        ((IMessage) cm.getModel()).setArgument("produto, quantidade");
        ILinkPresentation m4 = msg("consultarCarrinho", "", pg, cr, 350);
        ret("itemAdicionado", m4);
        msg("exibirConfirmacao", "", pg, cp, 440);
    }

    static void seq04() throws Exception {
        IPackage p = pkg(4);
        IClass consumidor = uce.createActor(p, "Consumidor");
        IClass pagCarr = cls(p, "PaginaDoCarrinho");
        IClass carrinho = cls(p, "CarrinhoDeCompras");
        IClass itemCls = cls(p, "ItemDoCarrinho");

        ISequenceDiagram d = sde.createSequenceDiagram(project, title(4));
        INodePresentation cp = lifeActor(consumidor, 50, 650);
        INodePresentation pg = life(pagCarr, "", 320, 650);
        INodePresentation cr = life(carrinho, "", 650, 650);
        INodePresentation it = life(itemCls, "item", 950, 650);

        msg("clicarRemover", "item", cp, pg, 100);
        msg("removerItem", "item", pg, cr, 165);
        ILinkPresentation m3 = msg("localizarItem", "", cr, cr, 240);
        shiftLabelX(m3, -150);
        ILinkPresentation dm = sde.createDestroyMessage("destruir", cr, it, 320);
        ((IMessage) dm.getModel()).setArgument("item");
        ILinkPresentation m5 = msg("consultarRemocao", "", pg, cr, 400);
        ret("itemRemovido", m5);
        msg("exibirConfirmacao", "", pg, cp, 485);
    }

    static void seq05() throws Exception {
        IPackage p = pkg(5);
        IClass cliente = uce.createActor(p, "Cliente");
        IClass carrinho = cls(p, "CarrinhoDeCompras");
        IClass itemCls = cls(p, "Item");
        IClass gateway = cls(p, "GatewayDePagamento");
        IClass ordem = cls(p, "OrdemDeEntrega");

        ISequenceDiagram d = sde.createSequenceDiagram(project, title(5));
        INodePresentation cl = lifeActor(cliente, 50, 850);
        INodePresentation cr = life(carrinho, "", 280, 850);
        INodePresentation it = life(itemCls, "item", 540, 850);
        INodePresentation gw = life(gateway, "", 780, 850);
        INodePresentation od = life(ordem, "", 1020, 850);

        msg("finalizarCompra", "", cl, cr, 100);
        msg("solicitarConfirmacao", "", cr, cl, 165);
        msg("confirmarCompra", "", cl, cr, 230);

        // loop messages
        ILinkPresentation m4 = msg("obterPreco", "", cr, it, 320);
        ILinkPresentation r4 = ret("preco", m4);
        ILinkPresentation m5 = msg("processarPagamento", "valor", cr, gw, 390);
        ILinkPresentation r5 = ret("pagamentoAprovado", m5);
        ILinkPresentation m6 = msg("criarOrdemDeEntrega", "pedido", cr, od, 480);
        ILinkPresentation r6 = ret("ordemCriada", m6);

        // outside loop
        msg("compraFinalizada", "", cr, cl, 620);

        // inner opt first, then outer loop
        IPresentation[] optTargets = new IPresentation[]{cl, cr, it, gw, od, m6, r6};
        INodePresentation opt = sde.createCombinedFragment("opt", optTargets);
        ((ICombinedFragment) opt.getModel()).getInteractionOperands()[0].setGuard("pagamento aprovado");

        IPresentation[] loopTargets = new IPresentation[]{cl, cr, it, gw, od, m4, r4, m5, r5, opt};
        INodePresentation loop = sde.createCombinedFragment("loop", loopTargets);
        ((ICombinedFragment) loop.getModel()).getInteractionOperands()[0].setGuard("para cada Item no carrinho");
    }

    // =========================================================
    static void cls06() throws Exception {
        IPackage p = pkg(6);
        IClass conta = cls(p, "Conta");
        conta.setAbstract(true);
        IClass cc = cls(p, "ContaCorrente");
        IClass cp = cls(p, "ContaPoupanca");

        at(conta, "saldo", "double", INamedElement.PROTECTED_VISIBILITY);
        IOperation calc = op(conta, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY);
        calc.setAbstract(true);
        op(conta, "Conta", "", INamedElement.PUBLIC_VISIBILITY);

        op(cc, "ContaCorrente", "", INamedElement.PUBLIC_VISIBILITY);
        op(cc, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY);
        op(cp, "ContaPoupanca", "", INamedElement.PUBLIC_VISIBILITY);
        op(cp, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY);

        IGeneralization g1 = bme.createGeneralization(cc, conta, "");
        IGeneralization g2 = bme.createGeneralization(cp, conta, "");

        IClassDiagram d = cde.createClassDiagram(project, title(6));
        INodePresentation cn = node(conta, 460, 50);
        INodePresentation ccn = node(cc, 160, 360);
        INodePresentation cpn = node(cp, 760, 360);
        link(g1, cn, ccn);
        link(g2, cn, cpn);
        note("Conta é abstrata:\ncalcularTaxa() deve ser\nimplementada pelas subclasses.\n# saldo é protegido.",
             940, 90, cn);
        note("ContaCorrente herda de Conta\ne implementa calcularTaxa().", 60, 520, ccn);
        note("ContaPoupanca herda de Conta\ne implementa calcularTaxa().", 760, 520, cpn);
        hideFrame(d);
    }

    static void cls07() throws Exception {
        IPackage p = pkg(7);
        IClass logavel = iface(p, "Logavel");
        op(logavel, "log", "", INamedElement.PUBLIC_VISIBILITY);
        par(logavel.getOperations()[0], "mensagem", "String");
        IClass arq = cls(p, "LogEmArquivo");
        op(arq, "LogEmArquivo", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation alog = op(arq, "log", "", INamedElement.PUBLIC_VISIBILITY);
        par(alog, "mensagem", "String");
        IClass banco = cls(p, "LogEmBancoDeDados");
        op(banco, "LogEmBancoDeDados", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation blog = op(banco, "log", "", INamedElement.PUBLIC_VISIBILITY);
        par(blog, "mensagem", "String");
        IClass serv = cls(p, "ServicoDeAplicacao");
        at(serv, "logavel", "Logavel", INamedElement.PRIVATE_VISIBILITY);
        op(serv, "ServicoDeAplicacao", "", INamedElement.PUBLIC_VISIBILITY);

        IRealization r1 = bme.createRealization(arq, logavel, "");
        IRealization r2 = bme.createRealization(banco, logavel, "");
        IDependency dep = bme.createDependency(serv, logavel, "usa");

        IClassDiagram d = cde.createClassDiagram(project, title(7));
        INodePresentation sn = node(serv, 70, 70);
        INodePresentation ln = node(logavel, 480, 230);
        INodePresentation an = node(arq, 300, 500);
        INodePresentation bn = node(banco, 650, 500);
        normal(ln);
        ILinkPresentation r1l = link(r1, ln, an);
        ILinkPresentation r2l = link(r2, ln, bn);
        ILinkPresentation dl = link(dep, sn, ln);
        note("Dependência via atributo:\n- logavel : Logavel", 780, 60, dl);
        note("As classes implementam a interface\nLogavel (realização tracejada).", 780, 340, r2l);
        hideFrame(d);
    }

    static void cls08() throws Exception {
        IPackage p = pkg(8);
        IClass alg = iface(p, "AlgoritmoDeCompressao");
        IOperation aop = op(alg, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        par(aop, "dados", "String");
        IClass comp = cls(p, "CompressorDeArquivos");
        at(comp, "algoritmo", "AlgoritmoDeCompressao", INamedElement.PRIVATE_VISIBILITY);
        op(comp, "CompressorDeArquivos", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation cop = op(comp, "comprimirArquivo", "", INamedElement.PUBLIC_VISIBILITY);
        par(cop, "nome", "String");
        IClass zip = cls(p, "CompressaoZIP");
        op(zip, "CompressaoZIP", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation zop = op(zip, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        par(zop, "dados", "String");
        IClass rar = cls(p, "CompressaoRAR");
        op(rar, "CompressaoRAR", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation rop = op(rar, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        par(rop, "dados", "String");

        IAssociation assoc = bme.createAssociation(comp, alg, "usa", "", "");
        assoc.getMemberEnds()[1].setMultiplicityString("1");
        IRealization rz1 = bme.createRealization(zip, alg, "");
        IRealization rz2 = bme.createRealization(rar, alg, "");

        IClassDiagram d = cde.createClassDiagram(project, title(8));
        INodePresentation cn = node(comp, 80, 270);
        INodePresentation an = node(alg, 520, 50);
        INodePresentation zn = node(zip, 400, 470);
        INodePresentation rn = node(rar, 700, 470);
        normal(an);
        ILinkPresentation al = link(assoc, cn, an);
        link(rz1, an, zn);
        link(rz2, an, rn);
        note("Strategy: o contexto usa a interface\nAlgoritmoDeCompressao; os algoritmos\nsão intercambiáveis.", 880, 180, al);
        note("A associação permite trocar\no algoritmo em tempo de execução.", 60, 560, cn);
        hideFrame(d);
    }

    static void cls09() throws Exception {
        IPackage p = pkg(9);
        IClass inte = iface(p, "Interessado");
        IOperation iop = op(inte, "atualizar", "", INamedElement.PUBLIC_VISIBILITY);
        par(iop, "novoPreco", "double");
        IClass prod = cls(p, "ProdutoLeilao");
        at(prod, "preco", "double", INamedElement.PRIVATE_VISIBILITY);
        at(prod, "interessados", "List<Interessado>", INamedElement.PRIVATE_VISIBILITY);
        op(prod, "ProdutoLeilao", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation add = op(prod, "adicionarInteressado", "", INamedElement.PUBLIC_VISIBILITY);
        par(add, "interessado", "Interessado");
        IOperation rem = op(prod, "removerInteressado", "", INamedElement.PUBLIC_VISIBILITY);
        par(rem, "interessado", "Interessado");
        op(prod, "notificarInteressados", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation setp = op(prod, "setPreco", "", INamedElement.PUBLIC_VISIBILITY);
        par(setp, "novoPreco", "double");
        IClass comp = cls(p, "Comprador");
        op(comp, "Comprador", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation cop = op(comp, "atualizar", "", INamedElement.PUBLIC_VISIBILITY);
        par(cop, "novoPreco", "double");

        IAssociation agg = bme.createAssociation(prod, inte, "observa", "", "");
        agg.getMemberEnds()[1].setMultiplicityString("0..*");
        agg.getMemberEnds()[0].setAggregation();
        IRealization real = bme.createRealization(comp, inte, "");
        IDependency dep = bme.createDependency(comp, prod, "se registra");

        IClassDiagram d = cde.createClassDiagram(project, title(9));
        INodePresentation pn = node(prod, 70, 80);
        INodePresentation inn = node(inte, 690, 110);
        INodePresentation cn = node(comp, 880, 470);
        normal(inn);
        ILinkPresentation agl = link(agg, pn, inn);
        link(real, inn, cn);
        ILinkPresentation dl = link(dep, cn, pn);
        note("ProdutoLeilao mantém 0..* Interessados\ne os notifica quando o preço muda.", 70, 500, pn);
        note("Interessado define atualizar(novoPreco);\nComprador implementa a interface.", 1000, 60, inn);
        hideFrame(d);
    }

    static void cls10() throws Exception {
        IPackage p = pkg(10);
        IClass editor = cls(p, "Editor");
        editor.setAbstract(true);
        IClass doc = cls(p, "Documento");
        doc.setAbstract(true);
        IClass et = cls(p, "EditorDeTexto");
        IClass ep = cls(p, "EditorDePlanilha");
        IClass dt = cls(p, "DocumentoDeTexto");
        IClass dp = cls(p, "DocumentoDePlanilha");

        op(editor, "Editor", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation criar = op(editor, "criarDocumento", "Documento", INamedElement.PUBLIC_VISIBILITY);
        criar.setAbstract(true);
        op(doc, "Documento", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation abrir = op(doc, "abrir", "", INamedElement.PUBLIC_VISIBILITY);
        abrir.setAbstract(true);

        op(et, "EditorDeTexto", "", INamedElement.PUBLIC_VISIBILITY);
        op(et, "criarDocumento", "DocumentoDeTexto", INamedElement.PUBLIC_VISIBILITY);
        op(ep, "EditorDePlanilha", "", INamedElement.PUBLIC_VISIBILITY);
        op(ep, "criarDocumento", "DocumentoDePlanilha", INamedElement.PUBLIC_VISIBILITY);
        op(dt, "DocumentoDeTexto", "", INamedElement.PUBLIC_VISIBILITY);
        op(dt, "abrir", "", INamedElement.PUBLIC_VISIBILITY);
        op(dp, "DocumentoDePlanilha", "", INamedElement.PUBLIC_VISIBILITY);
        op(dp, "abrir", "", INamedElement.PUBLIC_VISIBILITY);

        IGeneralization g1 = bme.createGeneralization(et, editor, "");
        IGeneralization g2 = bme.createGeneralization(ep, editor, "");
        IGeneralization g3 = bme.createGeneralization(dt, doc, "");
        IGeneralization g4 = bme.createGeneralization(dp, doc, "");
        IDependency d1 = bme.createDependency(et, dt, "cria");
        IDependency d2 = bme.createDependency(ep, dp, "cria");

        IClassDiagram d = cde.createClassDiagram(project, title(10));
        INodePresentation en = node(editor, 430, 50);
        INodePresentation etn = node(et, 100, 300);
        INodePresentation epn = node(ep, 720, 300);
        INodePresentation dn = node(doc, 430, 560);
        INodePresentation dtn = node(dt, 100, 810);
        INodePresentation dpn = node(dp, 720, 810);
        link(g1, en, etn);
        link(g2, en, epn);
        link(g3, dn, dtn);
        link(g4, dn, dpn);
        ILinkPresentation l1 = link(d1, etn, dtn);
        ILinkPresentation l2 = link(d2, epn, dpn);
        note("Factory Method: criarDocumento() é abstrato\ne define o ponto de criação do produto.", 940, 90, en);
        note("EditorDeTexto cria DocumentoDeTexto;\nEditorDePlanilha cria DocumentoDePlanilha.", 900, 430, l1);
        note("Subclasses concretas instanciam\no produto correspondente.", 60, 690, dn);
        hideFrame(d);
    }
}
