public class Pair implements java.lang.Cloneable, java.lang.Comparable
{
    private java.lang.Object a;
    private java.lang.Object b;

    public Pair(java.lang.Object a, java.lang.Object b)
    {
        super();

        a = (new CloneableReference(a)).clone();
        b = (new CloneableReference(b)).clone();
        return;
    }

    public java.lang.Object a()
    {
        return (new CloneableReference(a)).clone();
    }

    public java.lang.Object b()
    {
        return (new CloneableReference(b)).clone();
    }

    public java.lang.String toString()
    {
        java.lang.Object $r0, $r9;
        java.lang.String $r4, $r8, $r13;

        $r0 = a;
        $r4 = (new StringBuffer()).append("(").append($r0).toString();
        $r8 = (new StringBuffer()).append($r4).append(",").toString();
        $r9 = b;
        $r13 = (new StringBuffer()).append($r8).append($r9).toString();
        return (new StringBuffer()).append($r13).append(")").toString();
    }

    public java.lang.Object clone()
    {
        Pair clon;

        clon = null;

        label_0:
        {
            clon = (Pair) this.clone();
        }

        return clon;
    }

    public boolean equals(java.lang.Object o)
    {
        boolean $z1, $z2;
        Pair that;

        if (o instanceof Pair != false)
        {
            $z1 = false;
        }
        else
        {
            $z1 = true;
        }

        if ($z1 == false)
        {
            that = (Pair) o;

            label_1:
            {
                if (this.eq(a, that.a) != false)
                {
                    if (this.eq(b, that.b) != false)
                    {
                        $z2 = true;
                        break label_1;
                    }
                }

                $z2 = false;
            }

            return $z2;
        }
        else
        {
            return false;
        }
    }

    private boolean eq(java.lang.Object x, java.lang.Object y)
    {
        boolean $z0;

        label_2:
        {
            if (x == null)
            {
                if (y == null)
                {
                    $z0 = true;
                    break label_2;
                }
            }

            $z0 = false;
        }

        if ($z0 == false)
        {
            if (x != null)
            {
                if (y != null)
                {
                    return x.equals(y);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public int compareTo(java.lang.Object o)
    {
        Pair that;
        int c;

        that = (Pair) o;
        c = this.cmp(a, that.a);

        if (c == 0)
        {
            c = this.cmp(b, that.b);
        }

        return c;
    }

    private int 'cmp'(java.lang.Object x, java.lang.Object y)
    {
        boolean $z0;

        label_3:
        {
            if (x == null)
            {
                if (y == null)
                {
                    $z0 = true;
                    break label_3;
                }
            }

            $z0 = false;
        }

        if ($z0 == false)
        {
            if (x != null)
            {
                if (y != null)
                {
                    return ((Comparable) x).compareTo(y);
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return 0;
        }
    }

    public int hashCode()
    {
        int h, $i0, $i1;

        h = 17;
        h = h * 37;

        if (a != null)
        {
            $i0 = a.hashCode();
        }
        else
        {
            $i0 = 0;
        }

        h = h + $i0;
        h = h * 37;

        if (b != null)
        {
            $i1 = b.hashCode();
        }
        else
        {
            $i1 = 0;
        }

        h = h + $i1;
        return h;
    }
}
