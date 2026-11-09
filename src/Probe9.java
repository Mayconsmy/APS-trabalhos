import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;

public class Probe9 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe9.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        p("dgm");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 400); ((ILifeline)bp.getModel()).setBase(b);
        ap.setProperty("lifeline_length", "700"); bp.setProperty("lifeline_length", "700");
        p("lifelines");
        ILinkPresentation m1 = de.createMessage("fazer", ap, bp, 100);
        ((IMessage) m1.getModel()).setArgument("x");
        p("m1");
        ILinkPresentation m2 = de.createMessage("outro", ap, bp, 160);
        p("m2");
        ILinkPresentation m3 = de.createMessage("terceiro", bp, ap, 300);
        p("m3direct " + m3.getLabel());
        p("m3 skip dash");
        ILinkPresentation d1 = de.createMessage("dummy", ap, bp, 390);
        p("d1");
        d1.setProperty("line.color", "#ffffff");
        p("d1 color");
        d1.setProperty("name_visibility", "false");
        p("d1 nvis");
        ILinkPresentation r1 = de.createReturnMessage("resultado", d1);
        p("r1 label=" + r1.getLabel() + " points=" + java.util.Arrays.toString(r1.getPoints()));
        TransactionManager.endTransaction();
        pa.save();
        p("SAVED");
        pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
