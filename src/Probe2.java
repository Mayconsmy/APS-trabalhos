import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Probe2 {
    public static void main(String[] args) throws Exception {
        System.out.println("START");
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe2.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        IClass a = bme.createClass(project, "A");
        IClass b = bme.createClass(project, "B");
        SequenceDiagramEditor de = pa.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram dgm = de.createSequenceDiagram(project, "D");
        System.out.println("DGM");
        INodePresentation ap = de.createLifeline("", 50);
        System.out.println("LLa");
        INodePresentation bp = de.createLifeline("", 250);
        System.out.println("LLb");
        ILinkPresentation m = de.createMessage("msg1", ap, bp, 100);
        System.out.println("MSG1");
        ILinkPresentation r = de.createReturnMessage("ret1", m);
        System.out.println("RET1");
        TransactionManager.endTransaction();
        System.out.println("TXEND");
        pa.save();
        System.out.println("SAVED");
        dgm.exportImage("/home/maycon/Trabalho_Unidade02/img/probe2.png", "png", 3.0);
        System.out.println("EXPORTED");
        pa.close();
    }
}
