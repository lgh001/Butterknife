package cn.lgh.processor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import cn.lgh.annotation.BindView;

/**
 * @author lgh
 * @date 2020/6/9
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("cn.lgh.annotation.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyKnifeProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler=processingEnvironment.getFiler();
        mMessager=processingEnvironment.getMessager();
        mElementUtils=processingEnvironment.getElementUtils();
    }

//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.RELEASE_7;
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> sup=new HashSet<>();
//        sup.add(BindView.class.getName());
//        return sup;
//    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        for (Element element: elements){
            //1.获取包名
            PackageElement packageElement = mElementUtils.getPackageOf(element);
            String pkgName = packageElement.getQualifiedName().toString();
            System.out.println("package = "+pkgName);
            print("package = "+pkgName);
            //2.注解所在的类的类名
            TypeElement typeElement= (TypeElement) element.getEnclosingElement();
            String className = typeElement.getSimpleName().toString();
            System.out.println("enclosingClass = "+className);

            //因为BindView之作用于field，所以这里可以直接进行强转
            VariableElement bindViewElement= (VariableElement) element;
            //3.获取注解的成员变量名
            String fieldName = bindViewElement.getSimpleName().toString();
            //4.获取注解元数据
            BindView bindView = element.getAnnotation(BindView.class);
            int id=bindView.value();
            System.out.println(fieldName +" = "+ id);
            //5.生成文件
            createFile(pkgName,className,fieldName,id);
            return true;
        }


        return false;
    }

    private void createFile(String pkgName,String clzName,String filedName,int id){
        try {
            String newClassName=clzName+"$BindAdapterImp";
            JavaFileObject jfo = mFiler.createSourceFile(pkgName + "." + newClassName, new Element[]{});
            Writer writer=jfo.openWriter();
            writer.write("package "+pkgName+";");
            writer.write("\n\n");
            writer.write("import cn.lgh.butterknife.BindAdapter;");
            writer.write("\n\n\n");
            writer.write("public class "+newClassName+" implements BindAdapter<"+clzName+">{");
            writer.write("\n\n");
            writer.write("public void bind("+clzName+" target){");
            writer.write("\n");
            writer.write("target."+filedName+" = target.findViewById("+id+");");
            writer.write("\n");
            writer.write("  }");
            writer.write("\n\n");
            writer.write("}");
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void print(String msg){
        mMessager.printMessage(Diagnostic.Kind.NOTE,msg);
    }
}
