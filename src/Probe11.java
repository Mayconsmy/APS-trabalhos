import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;
import java.util.*;

public class Probe11 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe11.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 450); ((ILifeline)bp.getModel()).setBase(b);
        ap.setProperty("lifeline_length", "800"); bp.setProperty("lifeline_length", "800");
        ILinkPresentation m1 = de.createMessage("fazer", ap, bp, 100);
        ILinkPresentation d1 = de.createMessage("dummy", ap, bp, 390);
        try {
            d1.setAllPoints(new Point2D[]{new Point2D.Double(105, 390), new Point2D.Double(455, 390)});
            p("d1 setAllPoints OK " + Arrays.toString(d1.getAllPoints()));
        } catch (Throwable t) { p("d1 setAllPoints ERR " + t.getMessage()); }
        ILinkPresentation r1 = de.createReturnMessage("resultado", d1);
        p("r1 points=" + Arrays.toString(r1.getPoints()));
        // try delete dummy presentation
        try { de.deletePresentation(d1); p("deleted d1 presentation OK"); }
        catch (Throwable t) { p("delete d1 ERR " + t.getMessage()); }
        p("presentations:");
        for (IPresentation pr : dgm.getPresentations()) p("  " + pr.getType() + " [" + pr.getLabel() + "]");
        TransactionManager.endTransaction();
        pa.save();
        p("SAVED");
        pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
