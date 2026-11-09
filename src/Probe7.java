import java.awt.geom.Point2D;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe7 {
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe7.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 350); ((ILifeline)bp.getModel()).setBase(b);
        ap.setProperty("lifeline_length", "600"); bp.setProperty("lifeline_length", "600");
        ILinkPresentation m1 = de.createMessage("m1", ap, bp, 100);
        ILinkPresentation r1 = de.createReturnMessage("r1", m1);
        System.out.println("created r1 " + r1.getLabel());
        try {
            r1.setAllPoints(new Point2D[]{new Point2D.Double(350, 250), new Point2D.Double(50, 250)});
            System.out.println("setAllPoints OK " + java.util.Arrays.toString(r1.getAllPoints()));
        } catch (Throwable t) { System.out.println("setAllPoints ERR " + t); }
        try {
            r1.setPoints(new Point2D[]{new Point2D.Double(350, 300), new Point2D.Double(50, 300)});
            System.out.println("setPoints OK " + java.util.Arrays.toString(r1.getPoints()));
        } catch (Throwable t) { System.out.println("setPoints ERR " + t); }
        try {
            ILinkPresentation r2 = de.createReturnMessage("r2", m1);
            System.out.println("r2 OK " + r2.getLabel());
        } catch (Throwable t) { System.out.println("r2 ERR " + t); }
        try {
            m1.setProperty("visibility", "false");
            System.out.println("hide OK");
        } catch (Throwable t) { System.out.println("hide ERR " + t); }
        TransactionManager.endTransaction();
        pa.save();
        System.out.println("SAVED");
        pa.close();
    }
}
