import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.util.*;

public class Probe8 {
    static void dump(String tag, IPresentation p) {
        System.out.println("== " + tag + " type=" + p.getType());
        HashMap m = p.getProperties();
        List<String> keys = new ArrayList<String>(m.keySet());
        Collections.sort(keys);
        for (String k : keys) System.out.println("   " + k + " = " + m.get(k));
    }
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe8.asta");
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
        dump("m1", m1);
        ILinkPresentation m2 = de.createMessage("m2", bp, ap, 200);
        dump("m2 direct reverse", m2);
        ILinkPresentation r1 = de.createReturnMessage("r1", m1);
        dump("r1 return", r1);
        String[] cand = {"dashed","line.style","lineStyle","linestyle","dash","line.type","lineType","line"};
        for (String c : cand) {
            try { m2.setProperty(c, "true"); System.out.println("set " + c + "=true OK -> " + m2.getProperty(c)); }
            catch (Throwable t) { System.out.println("set " + c + " ERR " + t.getMessage()); }
        }
        for (String c : cand) {
            try { m1.setProperty(c, "true"); System.out.println("m1 set " + c + "=true OK -> " + m1.getProperty(c)); }
            catch (Throwable t) { System.out.println("m1 set " + c + " ERR " + t.getMessage()); }
        }
        try { m1.setProperty("visible", "false"); System.out.println("m1 visible=false OK"); } catch (Throwable t) { System.out.println("visible ERR " + t.getMessage()); }
        try { m1.setProperty("visibility", "false"); System.out.println("m1 visibility=false OK"); } catch (Throwable t) { System.out.println("visibility ERR " + t.getMessage()); }
        TransactionManager.endTransaction();
        pa.save();
        System.out.println("SAVED");
        pa.close();
    }
}
