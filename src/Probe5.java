import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe5 {
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe5.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass visitante = pa.getModelEditorFactory().getUseCaseModelEditor().createActor(project, "Visitante");
        IClass tela = bme.createClass(project, "TelaDeLogin");
        IClass sis = bme.createClass(project, "SistemaDeAutenticacao");
        IClass pag = bme.createClass(project, "PaginaPrincipal");
        IClass item = bme.createClass(project, "ItemDoCarrinho");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "Questao 02 - Teste");
        INodePresentation vp = de.createLifeline("", 50); ((ILifeline) vp.getModel()).setBase(visitante);
        INodePresentation tp = de.createLifeline("", 280); ((ILifeline) tp.getModel()).setBase(tela);
        INodePresentation sp = de.createLifeline("", 550); ((ILifeline) sp.getModel()).setBase(sis);
        INodePresentation pp = de.createLifeline("", 820); ((ILifeline) pp.getModel()).setBase(pag);
        for (INodePresentation l : new INodePresentation[]{vp,tp,sp,pp}) l.setProperty("lifeline_length", "700");

        ILinkPresentation m1 = de.createMessage("informarCredenciais", vp, tp, 100);
        ((IMessage) m1.getModel()).setArgument("email, senha");
        ILinkPresentation m2 = de.createMessage("autenticar", tp, sp, 160);
        ((IMessage) m2.getModel()).setArgument("email, senha");
        ILinkPresentation m3 = de.createMessage("verificarCredenciais", sp, sp, 220);
        ((IMessage) m3.getModel()).setArgument("email, senha");
        ILinkPresentation m4 = de.createMessage("mensagemDeSucesso", sp, tp, 300);
        ILinkPresentation r4 = de.createReturnMessage("mensagemDeSucesso", m4);
        ILinkPresentation m5 = de.createMessage("redirecionar", tp, pp, 360);
        ILinkPresentation r5 = de.createReturnMessage("exibirPaginaPrincipal()", m5);
        ILinkPresentation m6 = de.createMessage("erro", sp, tp, 440);
        ILinkPresentation r6 = de.createReturnMessage("erro", m6);
        ILinkPresentation m7 = de.createMessage("exibirMensagem", tp, vp, 500);
        ((IMessage) m7.getModel()).setArgument("\"Mensagem de Erro\"");
        System.out.println("labels: " + m1.getLabel()+" | "+r4.getLabel()+" | "+m7.getLabel());

        IPresentation[] targets1 = new IPresentation[]{vp, tp, sp, pp, m4, r4, m5, r5};
        INodePresentation cf = de.createCombinedFragment("alt", targets1);
        System.out.println("CF " + cf.getLocation() + " " + cf.getWidth() + "x" + cf.getHeight());
        ICombinedFragment frag = (ICombinedFragment) cf.getModel();
        frag.getInteractionOperands()[0].setGuard("[credenciais válidas]");
        IInteractionOperand op2 = de.addInteractionOperand(cf, new IPresentation[]{m6, r6, m7}, "", "[credenciais inválidas]");
        System.out.println("OP2 " + op2.getGuard());
        // print cf properties
        System.out.println("cf props " + cf.getProperties());

        // create/destroy test lifelines at far right
        INodePresentation t2 = de.createLifeline("", 1100); ((ILifeline) t2.getModel()).setBase(tela);
        INodePresentation ip = de.createLifeline("item", 1300); ((ILifeline) ip.getModel()).setBase(item);
        t2.setProperty("lifeline_length", "700"); ip.setProperty("lifeline_length", "700");
        ILinkPresentation cm = de.createCreateMessage("criar", t2, ip, 200);
        ((IMessage) cm.getModel()).setArgument("produto, quantidade");
        System.out.println("create label: " + cm.getLabel() + " st=" + java.util.Arrays.toString(cm.getModel().getStereotypes()));
        ILinkPresentation dm = de.createDestroyMessage("destroy", t2, ip, 400);
        System.out.println("destroy label: " + dm.getLabel() + " st=" + java.util.Arrays.toString(dm.getModel().getStereotypes()));

        // try explicit labels

        TransactionManager.endTransaction();
        pa.save();
        System.out.println("BOUND " + dgm.getBoundRect());
        for (IPresentation p : dgm.getPresentations()) {
            if (p instanceof INodePresentation) {
                INodePresentation n = (INodePresentation)p;
                System.out.println(n.getType()+" ["+n.getLabel()+"] "+n.getLocation()+" "+n.getWidth()+"x"+n.getHeight());
            }
        }
        pa.close();
    }
}
