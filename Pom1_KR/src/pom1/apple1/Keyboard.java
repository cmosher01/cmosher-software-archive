// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Keyboard.java

package pom1.apple1;

import java.awt.event.KeyEvent;

public class Keyboard
{
    public Keyboard()
    {
    }

    public static int kbdTranslator(KeyEvent e)
    {
        char key = e.getKeyChar();
        return kbdTranslator(key);
    }

    public static int kbdTranslator(char key)
    {
        key = Character.toUpperCase(key);
        if((key > '@') & (key < '['))
            return key + 128;
        if((key > '/') & (key < ':'))
            return key + 128;
        if(key == '\n')
            return 141;
        if(key == '\033')
            return 155;
        if(key == '\b')
            return 136;
        if(key == '_')
            return 223;
        if(key == ' ')
            return 160;
        if(key == ',')
            return 172;
        if(key == ']')
            return 221;
        if(key == '[')
            return 219;
        if(key == '.')
            return 174;
        if(key == '/')
            return 175;
        if(key == '+')
            return 171;
        if(key == '*')
            return 170;
        if(key == '@')
            return 192;
        if(key == '!')
            return 161;
        if(key == ':')
            return 186;
        if(key == 0)
            return 222;
        if(key == '(')
            return 168;
        if(key == ')')
            return 169;
        if(key == '%')
            return 165;
        if(key == ';')
            return 187;
        if(key == '<')
            return 188;
        if(key == '=')
            return 189;
        if(key == '>')
            return 190;
        if(key == '?')
            return 191;
        if(key == '\\')
            return 220;
        if(key == '$')
            return 164;
        if(key == '"')
            return 162;
        if(key == '#')
            return 163;
        if(key == '&')
            return 166;
        if(key == '\'')
            return 167;
        if(key == '-')
            return 173;
        if(key==8)
        	return 0xdf;
        if(key == '\021')
            return 13;
        if((key > 0) & (key < '\033'))
            return key + 128;
        return -1;
    }
}
