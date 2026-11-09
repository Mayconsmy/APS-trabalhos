import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
public class WidthTest {
  public static void main(String[] a) throws Exception {
    ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
    pa.open(a[0], true, false, true);
    TransactionManager.beginTransaction();
    for (IDiagram d : pa.getProject().getDiagrams()) {
      if (!(d instanceof ISequenceDiagram)) continue;
      for (IPresentation p : d.getPresentations()) {
        if ("Lifeline".equals(p.getType())) {
          INodePresentation n=(INodePresentation)p;
          String lbl=n.getLabel();
          if (lbl != null && lbl.length() > 8) {
            int w = Math.max(120, 55 + 7*lbl.length());
            try { n.setWidth(w); System.out.println(d.getName()+" ["+lbl+"] -> "+w); } catch (Throwable t) { System.out.println("ERR "+t.getMessage()); }
          }
        }
      }
    }
    TransactionManager.endTransaction();
    pa.saveAs(a[1]);
    pa.close();
  }
}
