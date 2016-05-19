package util;

import app.playground1.CreateTableExample;
import app.playground1.DropTableExample;

public class Clean
{

    public static void main(String[] args)
    {
        DropTableExample.main( args );
        CreateTableExample.main( args );
    }

}
