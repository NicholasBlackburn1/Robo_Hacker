package blackburn.python;
import org.python.util.PythonInterpreter;

import blackburn.BlackburnConst;
import net.optifine.Log;
public class ModLoader {

    public static void loadPyFiles(){
        Log.error("in py modloader");
        try(PythonInterpreter pyInterp = new PythonInterpreter()) {
                 pyInterp.exec("print('Hello Python World!')");
    }
    }
    
    
}
