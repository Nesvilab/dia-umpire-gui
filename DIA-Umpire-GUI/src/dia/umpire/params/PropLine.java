/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.params;

/**
 *
 * @author dmitriya
 */
public class PropLine {
    String justALine;
    String name;
    String value;
    String comment;

    public String getJustALine() {
        return justALine;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }
    
    

    public PropLine(String justALine, String name, String value, String comment) {
        this.justALine = justALine;
        this.name = name;
        this.value = value;
        this.comment = comment;
    }

    public boolean isSimpleLine() {
        return justALine != null;
    }

    @Override
    public String toString() {
        if (isSimpleLine())
            return "TextLine{" + justALine + "}";

        return "PropLine{" +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}