import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe {
    static ProjectAccessor pa;
    static BasicModelEditor bme;

    public static void main(String[] args) throws Exception {
        System.out.println("START");
        pa = AstahAPI.getAstahAPI().getProjectAccessor();
        System.out.println("ACCESSOR " + pa);
        pa.create("/tmp/probe.asta");
        System.out.println("CREATED");
        IModel project = pa.getProject();
        System.out.println("PROJECT " + project);
        bme = ModelEditorFactory.getBasicModelEditor();
        System.out.println("BME");
        TransactionManager.beginTransaction();
        System.out.println("TX");

        UseCaseModelEditor ucme = pa.getModelEditorFactory().getUseCaseModelEditor();
        System.out.println("UCME " + ucme);
        IClass actor = ucme.createActor(project, "Cliente");
        System.out.println("ACTOR");
        IClass atm = bme.createClass(project, "CaixaEletronico");
        System.out.println("ATM");
        IClass banco = bme.createClass(project, "Banco");
        IClass item = bme.createClass(project, "ItemDoCarrinho");
        System.out.println("CLASSES");

        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        System.out.println("DE " + de);
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "Questao 01 - Teste");
        System.out.println("DGM");

        INodePresentation actorPs = de.createLifeline("", 50);
        System.out.println("LL1");
        ((ILifeline) actorPs.getModel()).setBase(actor);
        System.out.println("BASE1");
        INodePresentation atmPs = de.createLifeline("", 250);
        ((ILifeline) atmPs.getModel()).setBase(atm);
        INodePresentation bancoPs = de.createLifeline("", 450);
        System.out.println("LL3");
        ((ILifeline) bancoPs.getModel()).setBase(banco);
        INodePresentation itemPs = de.createLifeline("item", 650);
        System.out.println("LL4");
        ((ILifeline) itemPs.getModel()).setBase(item);

        System.out.println("BEFORE MSG");
        ILinkPresentation m1 = de.createMessage("inserirCartao(cartao)", actorPs, atmPs, 120);
        ILinkPresentation m2 = de.createReturnMessage("solicitarSenha()", m1);
        ILinkPresentation m3 = de.createMessage("validarSenha(cartao, senha) : boolean", atmPs, bancoPs, 200);
        ILinkPresentation m4 = de.createReturnMessage("senhaValida : boolean", m3);
        ILinkPresentation m5 = de.createCreateMessage("<<create>> criar(produto, quantidade)", atmPs, itemPs, 300);
        ILinkPresentation m6 = de.createDestroyMessage("<<destroy>>", atmPs, itemPs, 400);
        de.createTermination(itemPs);

        // combined fragment test
        INodePresentation cf = de.createCombinedFragment("", "alt", new Point2D.Double(20, 500), 500, 150);
        ICombinedFragment frag = (ICombinedFragment) cf.getModel();
        frag.getInteractionOperands()[0].setGuard("credenciais validas");
        frag.addInteractionOperand("", "[credenciais invalidas]");
        cf.setProperty("operand.1.length", "70");
        ILinkPresentation m7 = de.createMessage("redirecionar()", atmPs, bancoPs, 530);
        ILinkPresentation m8 = de.createReturnMessage("exibirPaginaPrincipal()", m7);
        ILinkPresentation m9 = de.createMessage("erro", atmPs, bancoPs, 610);
        de.createReturnMessage("exibirMensagem(\"Mensagem de Erro\")", m9);

        TransactionManager.endTransaction();
        pa.save();
        dgm.exportImage("/home/maycon/Trabalho_Unidade02/img/probe.png", "png", 3.0);
        System.out.println("frame type: " + findType(dgm, "Frame"));
        for (IPresentation p : dgm.getPresentations()) {
            if (p instanceof INodePresentation) {
                INodePresentation n = (INodePresentation) p;
                System.out.println(n.getType() + " label=[" + n.getLabel() + "] loc=" + n.getLocation() + " w=" + n.getWidth() + " h=" + n.getHeight());
            } else if (p instanceof ILinkPresentation) {
                ILinkPresentation l = (ILinkPresentation) p;
                System.out.println("LINK " + p.getType() + " label=[" + l.getLabel() + "] model=" + l.getModel().getClass().getSimpleName());
            }
        }
        pa.close();
    }

    static String findType(ISequenceDiagram d, String t) throws Exception {
        for (IPresentation p : d.getPresentations()) if (p.getType().equals(t)) return p.getLabel();
        return "none";
    }
}
