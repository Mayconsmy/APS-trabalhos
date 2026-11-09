import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;
import java.util.*;

public class Probe12 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe12.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap = de.createLifeline("", 50); ((ILifeline)ap.getModel()).setBase(a);
        INodePresentation bp = de.createLifeline("", 450); ((ILifeline)bp.getModel()).setBase(b);
        ap.setProperty("lifeline_length", "900"); bp.setProperty("lifeline_length", "900");
        for (int i=0;i<4;i++) de.createMessage("m" + (i+1), ap, bp, 100 + i*120);
        List<IPresentation> acts = new ArrayList<IPresentation>();
        for (IPresentation pr : dgm.getPresentations()) if ("Activation".equals(pr.getType())) acts.add(pr);
        p("acts=" + acts.size());
        // m1: line.width=0
        for (int i=0;i<2;i++) { try { acts.get(i).setProperty("line.width", "0"); } catch (Throwable t) { p("m1 width ERR " + t.getMessage()); } }
        // m2: white + width 0 + visibility false
        for (int i=2;i<4;i++) { try { acts.get(i).setProperty("line.width", "0"); acts.get(i).setProperty("line.color", "#ffffff"); acts.get(i).setProperty("visibility", "false"); } catch (Throwable t) { p("m2 ERR " + t.getMessage()); } }
        // m3: setLocation
        try { ((INodePresentation)acts.get(4)).setLocation(new Point2D.Double(-200, 340)); p("loc OK"); } catch (Throwable t) { p("loc ERR " + t.getMessage()); }
        // m4: opacity 0
        for (int i=6;i<8;i++) { try { acts.get(i).setProperty("alpah", "0"); p("alpha OK"); } catch (Throwable t) { p("alpha ERR " + t.getMessage()); } }
        TransactionManager.endTransaction();
        pa.save();
        p("SAVED");
        pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
