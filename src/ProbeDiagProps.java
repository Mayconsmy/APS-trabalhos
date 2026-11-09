import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.util.*;

public class ProbeDiagProps {
    static void walk(INamedElement e) throws Exception {
        if (e instanceof IDiagram) {
            IDiagram d = (IDiagram)e;
            System.out.println("DIAGRAM [" + d.getName() + "] owner=" + (d.getOwner()==null?"null":d.getOwner().getClass().getSimpleName()+":"+((INamedElement)d.getOwner()).getName()));
            HashMap m = d.getProperties();
            for (Object k : new TreeMap(m).keySet()) System.out.println("   " + k + " = " + m.get(k));
            for (IPresentation p : d.getPresentations()) {
                if ("Frame".equals(p.getType())) {
                    System.out.println("   FRAME label=[" + p.getLabel() + "] props=" + p.getProperties());
                }
            }
        }
        if (e instanceof IPackage) for (INamedElement c : ((IPackage)e).getOwnedElements()) walk(c);
    }
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.open(args[0], true, false, true);
        walk(pa.getProject());
        pa.close();
    }
}
