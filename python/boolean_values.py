x = False
if not x:
    print("boolean: not False")
x = True
if x:
    print("boolean: True")

x = []
if not x:
    print("list: not empty")
x = ["x"]
if x:
    print("list: at least one item")

x = None
if not x:
    print("None: not None")

x = ""
if not x:
    print("string: not zero-length")
x = "False"
if x:
    print("string: at least one character")

x = dict()
if not x:
    print("dict: not empty")
x["x"] = "x"
if x:
    print("dict: at least one item")

x = 0
if not x:
    print("int: not 0")
x = 3
if x:
    print("int: non-zero")

x = 0.0
if not x:
    print("float: not 0.0")
x = 0.3
if x:
    print("float: non-zero")
