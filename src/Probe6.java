import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe6 {
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe6.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        IClass c = bme.createClass(project, "C");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 350); ((ILifeline)bp.getModel()).setBase(b);
        INodePresentation cp = de.createLifeline("", 650); ((ILifeline)cp.getModel()).setBase(c);
        for (INodePresentation l : new INodePresentation[]{ap,bp,cp}) l.setProperty("lifeline_length", "700");

        ILinkPresentation m1 = de.createMessage("m1", ap, bp, 100);
        ILinkPresentation r1 = de.createReturnMessage("solicitarSenha()", m1);
        System.out.println("r1 points before: " + java.util.Arrays.toString(r1.getPoints()));
        System.out.println("r1 all before: " + java.util.Arrays.toString(r1.getAllPoints()));
        System.out.println("r1 label=" + r1.getLabel() + " props=" + r1.getProperties());
        // move return down
        Point2D[] pts = r1.getPoints();
        for (int i=0;i<pts.length;i++) System.out.println("pt"+i+"="+pts[i]);
        r1.setPoints(new Point2D[]{new Point2D.Double(350, 300), new Point2D.Double(50, 300)});
        System.out.println("r1 points after: " + java.util.Arrays.toString(r1.getPoints()));
        r1.setLabel("senhaValida : boolean");
        System.out.println("r1 label after: " + r1.getLabel());

        ILinkPresentation m2 = de.createMessage("m2", ap, cp, 400);
        ILinkPresentation r2 = de.createReturnMessage("exibirPaginaPrincipal()", m2);
        // change return endpoints to bp -> ap
        r2.setPoints(new Point2D[]{new Point2D.Double(350, 500), new Point2D.Double(50, 500)});
        System.out.println("r2 label=" + r2.getLabel());

        TransactionManager.endTransaction();
        pa.save();
        pa.close();
    }
}
