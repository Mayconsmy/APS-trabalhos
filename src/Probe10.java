import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.util.*;

public class Probe10 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe10.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 400); ((ILifeline)bp.getModel()).setBase(b);
        ap.setProperty("lifeline_length", "700"); bp.setProperty("lifeline_length", "700");
        ILinkPresentation m1 = de.createMessage("fazer", ap, bp, 100);
        ((IMessage) m1.getModel()).setArgument("x");
        ILinkPresentation m2 = de.createMessage("outro", ap, bp, 160);
        ILinkPresentation d1 = de.createMessage("dummy", ap, bp, 390);
        d1.setProperty("line.color", "#ffffff");
        d1.setProperty("name_visibility", "false");
        ILinkPresentation r1 = de.createReturnMessage("resultado", d1);
        p("before delete: " + dgm.getPresentations().length);
        List<IPresentation> acts = new ArrayList<IPresentation>();
        for (IPresentation pr : dgm.getPresentations()) {
            if ("Activation".equals(pr.getType())) acts.add(pr);
        }
        p("activations=" + acts.size());
        for (IPresentation pr : acts) {
            try { pr.setProperty("visibility", "false"); p("vis false OK on " + pr.getID()); }
            catch (Throwable t) { p("vis false ERR " + t.getMessage()); }
            try { pr.setProperty("fill.color", "#ffffff"); pr.setProperty("line.color", "#ffffff"); p("white OK"); }
            catch (Throwable t) { p("white ERR " + t.getMessage()); }
        }
        p("after props: " + dgm.getPresentations().length);
        for (IPresentation pr : dgm.getPresentations()) p("  " + pr.getType() + " [" + pr.getLabel() + "]");
        TransactionManager.endTransaction();
        pa.save();
        p("SAVED");
        pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
