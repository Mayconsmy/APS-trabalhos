import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe3 {
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "class";
        System.out.println("START " + mode);
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe3_" + mode + ".asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        IClass actor = pa.getModelEditorFactory().getUseCaseModelEditor().createActor(project, "Cliente");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        INodePresentation ap, bp;
        if (mode.equals("actor")) {
            ap = de.createLifeline("", 50);
            ((ILifeline) ap.getModel()).setBase(actor);
        } else {
            ap = de.createLifeline("", 50);
            if (mode.equals("base")) ((ILifeline) ap.getModel()).setBase(a);
        }
        System.out.println("LLa");
        bp = de.createLifeline("", 250);
        if (mode.equals("base") || mode.equals("actor")) ((ILifeline) bp.getModel()).setBase(b);
        System.out.println("LLb");
        ILinkPresentation m = de.createMessage("msg1", ap, bp, 100);
        System.out.println("MSG1");
        ILinkPresentation r = de.createReturnMessage("ret1", m);
        System.out.println("RET1");
        if (mode.equals("actor")) {
            ILinkPresentation cr = de.createCreateMessage("criar()", bp, ap, 200);
            System.out.println("CREATE");
        }
        TransactionManager.endTransaction();
        pa.save();
        System.out.println("SAVED");
        pa.close();
    }
}
