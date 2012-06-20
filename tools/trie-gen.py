import json, sys, time

def main():
    with open(sys.argv[1]) as infile:
        config = json.load(infile)
    now = time.strftime("%c", time.gmtime())
    sys.stdout.write(config['pre'])
    print("public class {} {{".format(config['class']))
    for group in config['groups']:
        print("\tpublic static {} lookup{}(String input) {{".format(group['return_type'], group['name']))
        print("\t\tfinal int length = input.length();")
        generateTrie(group['pairs']).emitCode(group['default'], 2, 0)
        print("\t}")
    print("}")

def emitCode(trie):
    print(trie)

class Node:
    def __init__(self, name, children):
        self.name = name
        self.children = children
        self.match = None

    def lookup(self, key):
        if len(key) == 0:
            return self
        else:
            next = None
            for child in self.children:
                if child.name == key[0]:
                    next = child
                    break
            assert(next)
            return next.lookup(key[1:])

    def compress(self):
        if len(self.children) == 1:
            child = self.children[0]
            while len(child.children) == 1 and child.match == None:
                subchild = child.children[0]
                child.name += subchild.name
                child.children = subchild.children
                child.match = subchild.match
        for child in self.children:
            child.compress()

    def emitCode(self, default, tabdepth, strlen):
        indent = "\t" * tabdepth
        if self.match:
            print(indent + "if (length == {}) {{".format(strlen))
            print(indent + "\treturn {};".format(self.match))
            print(indent + "}")

        if len(self.children) == 1:
            child = self.children[0]
            print(indent + "if (input.startsWith(\"{}\", {})) {{".format(child.name, strlen))
            child.emitCode(default, tabdepth + 1, strlen + len(child.name))
            print(indent + "} else {")
            print(indent + "\treturn {};".format(default))
            print(indent + "}");
        elif len(self.children) > 1:
            print(indent + "switch (input.charAt({})) {{".format(strlen))
            for child in self.children:
                print(indent + "case '{}':".format(child.name))
                child.emitCode(default, tabdepth + 1, strlen + 1)
            print(indent + "default:")
            print(indent + "\treturn {};".format(default));
            print(indent + "}")

        else:
            print(indent + "return {};".format(default))


    def depthStr(self, depth):
        ret = "\t" * depth
        ret += self.name
        if self.match:
            ret += " = " + self.match
        ret += "\n"
        for child in self.children:
            ret += child.depthStr(depth + 1)
        return ret

    def __str__(self):
        return self.depthStr(0)

def generateTrie(pairs):
    root = generateNode("", list(pairs.keys()))
    attachValues(root, pairs)
    root.compress()
    return root

def generateNode(name, strings):
    firsts = list(set(s[0] for s in strings if len(s) > 0))
    children = []
    for first in firsts:
        substrings = [s[1:] for s in strings if s and s[0] == first]
        children.append(generateNode(first, substrings))
    node = Node(name, children)
    return node

def attachValues(tree, pairs):
    for key, value in pairs.items():
        node = tree.lookup(key)
        assert(node.match == None)
        node.match = value

main()
