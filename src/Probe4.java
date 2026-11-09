import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe4 {
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe4.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass actor = pa.getModelEditorFactory().getUseCaseModelEditor().createActor(project, "Cliente");
        IClass atm = bme.createClass(project, "CaixaEletronico");
        IClass banco = bme.createClass(project, "Banco");
        IClass item = bme.createClass(project, "ItemDoCarrinho");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "Questao 01 - Teste");
        INodePresentation ap = de.createLifeline("", 50);
        ((ILifeline) ap.getModel()).setBase(actor);
        INodePresentation bp = de.createLifeline("", 300);
        ((ILifeline) bp.getModel()).setBase(atm);
        INodePresentation cp = de.createLifeline("", 550);
        ((ILifeline) cp.getModel()).setBase(banco);
        INodePresentation dp = de.createLifeline("item", 780);
        ((ILifeline) dp.getModel()).setBase(item);
        for (INodePresentation l : new INodePresentation[]{ap,bp,cp,dp}) l.setProperty("lifeline_length", "720");

        ILinkPresentation m1 = de.createMessage("inserirCartao", ap, bp, 100);
        ((IMessage) m1.getModel()).setArgument("cartao");
        ILinkPresentation r1 = de.createReturnMessage("solicitarSenha", m1);
        ILinkPresentation m2 = de.createMessage("informarSenha", ap, bp, 220);
        ((IMessage) m2.getModel()).setArgument("senha");
        System.out.println("labels: " + m1.getLabel() + " | " + r1.getLabel() + " | " + m2.getLabel());
        System.out.println("ret label check: " + dgm.getPresentations().length);

        ILinkPresentation c1 = de.createCreateMessage("criar", bp, dp, 320);
        IMessage cm = (IMessage) c1.getModel();
        cm.setArgument("produto, quantidade");
        cm.addStereotype("create");
        ILinkPresentation d1 = de.createDestroyMessage("destroy", bp, dp, 500);
        IMessage dm = (IMessage) d1.getModel();
        dm.addStereotype("destroy");
        de.createTermination(dp);
        System.out.println("create label: " + c1.getLabel() + " destroy: " + d1.getLabel());

        INodePresentation cf = de.createCombinedFragment("", "alt", new Point2D.Double(30, 560), 700, 240);
        ICombinedFragment frag = (ICombinedFragment) cf.getModel();
        frag.getInteractionOperands()[0].setGuard("[credenciais válidas]");
        frag.addInteractionOperand("", "[credenciais inválidas]");
        cf.setProperty("operand.1.length", "110");
        ILinkPresentation m3 = de.createMessage("mensagemDeSucesso", cp, bp, 600);
        ILinkPresentation r3 = de.createReturnMessage("mensagemDeSucesso", m3);
        ILinkPresentation m4 = de.createMessage("erro", cp, bp, 730);
        ILinkPresentation r4 = de.createReturnMessage("erro", m4);

        // frame size
        for (IPresentation p : dgm.getPresentations()) {
            if (p.getType().equals("Frame")) {
                INodePresentation f = (INodePresentation) p;
                f.setProperty("auto_resize", "false");
                f.setWidth(900);
                f.setHeight(900);
            }
        }
        TransactionManager.endTransaction();
        pa.save();
        System.out.println("BOUND " + dgm.getBoundRect());
        for (IPresentation p : dgm.getPresentations()) {
            System.out.println(p.getType() + " [" + p.getLabel() + "] " + (p instanceof INodePresentation ? ((INodePresentation)p).getLocation()+" "+((INodePresentation)p).getWidth()+"x"+((INodePresentation)p).getHeight() : ""));
        }
        pa.close();
    }
}
