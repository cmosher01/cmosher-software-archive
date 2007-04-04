/*
 * Created on Sep 23, 2005
 */

package nu.mine.mosher.oldexpr;


/*
expr: id
    | id . id ( args )
    | id . id
    | clas . id ( args )
    | num
args: expr argx
    | e
argx: , expr argx
    | e
clas: id
    | id . clas
*/
public class TemplateExpression
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	}
}
