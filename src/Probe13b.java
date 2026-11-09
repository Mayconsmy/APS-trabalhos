import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;

public class Probe13b {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    static IOperation op(BasicModelEditor b, IClass c, String n, String r, String v) throws Exception {
        IOperation o = b.createOperation(c, n, r); o.setVisibility(v); return o;
    }
    static IAttribute at(BasicModelEditor b, IClass c, String n, String t, String v) throws Exception {
        IAttribute a = b.createAttribute(c, n, t); a.setVisibility(v); return a;
    }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe13b.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        bme.setLanguageJava(project, true);
        IPackage pkg = bme.createPackage(project, "Q06");
        IClass conta = bme.createClass(pkg, "Conta"); conta.setAbstract(true);
        at(bme, conta, "saldo", "double", INamedElement.PROTECTED_VISIBILITY);
        IOperation calc = op(bme, conta, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY); calc.setAbstract(true);
        op(bme, conta, "Conta", "", INamedElement.PUBLIC_VISIBILITY);
        IClass cc = bme.createClass(pkg, "ContaCorrente");
        op(bme, cc, "ContaCorrente", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation ccalc = op(bme, cc, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY);
        IClass cp = bme.createClass(pkg, "ContaPoupanca");
        op(bme, cp, "ContaPoupanca", "", INamedElement.PUBLIC_VISIBILITY);
        IOperation pcalc = op(bme, cp, "calcularTaxa", "double", INamedElement.PUBLIC_VISIBILITY);
        IGeneralization g1 = bme.createGeneralization(cc, conta, "");
        IGeneralization g2 = bme.createGeneralization(cp, conta, "");
        ClassDiagramEditor de = pa.getDiagramEditorFactory().getClassDiagramEditor();
        IClassDiagram dgm = de.createClassDiagram(project, "Q06 Teste");
        INodePresentation cn = de.createNodePresentation(conta, new Point2D.Double(450,60));
        INodePresentation ccn = de.createNodePresentation(cc, new Point2D.Double(160,380));
        INodePresentation cpn = de.createNodePresentation(cp, new Point2D.Double(740,380));
        de.createLinkPresentation(g1, cn, ccn);
        de.createLinkPresentation(g2, cn, cpn);
        INodePresentation note = de.createNote("Conta é abstrata:\ncalcularTaxa() deve ser\nimplementada pelas subclasses.\n# saldo é protegido.", new Point2D.Double(900,100));
        de.createNoteAnchor(note, cn);
        TransactionManager.endTransaction();
        pa.save();
        p("BOUND " + dgm.getBoundRect());
        for (IPresentation pr : dgm.getPresentations()) {
            if (pr instanceof INodePresentation) { INodePresentation n=(INodePresentation)pr; p("NODE "+pr.getType()+" ["+n.getLabel()+"] "+n.getLocation()+" "+n.getWidth()+"x"+n.getHeight()); }
            else p("LINK "+pr.getType()+" ["+pr.getLabel()+"]");
        }
        p("SAVED"); pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
