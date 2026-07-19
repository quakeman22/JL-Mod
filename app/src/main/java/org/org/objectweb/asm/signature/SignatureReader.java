package org.objectweb.asm.signature;

/* JADX INFO: loaded from: classes.dex */
public class SignatureReader {
    private final String signatureValue;

    public SignatureReader(String signature) {
        this.signatureValue = signature;
    }

    public void accept(SignatureVisitor signatureVistor) {
        int offset;
        char currentChar;
        String signature = this.signatureValue;
        int length = signature.length();
        if (signature.charAt(0) == '<') {
            int offset2 = 2;
            while (true) {
                int classBoundStartOffset = signature.indexOf(58, offset2);
                signatureVistor.visitFormalTypeParameter(signature.substring(offset2 - 1, classBoundStartOffset));
                int offset3 = classBoundStartOffset + 1;
                char currentChar2 = signature.charAt(offset3);
                if (currentChar2 == 'L' || currentChar2 == '[' || currentChar2 == 'T') {
                    offset3 = parseType(signature, offset3, signatureVistor.visitClassBound());
                }
                while (true) {
                    offset = offset3 + 1;
                    currentChar = signature.charAt(offset3);
                    if (currentChar != ':') {
                        break;
                    } else {
                        offset3 = parseType(signature, offset, signatureVistor.visitInterfaceBound());
                    }
                }
                if (currentChar == '>') {
                    break;
                } else {
                    offset2 = offset;
                }
            }
        } else {
            offset = 0;
        }
        if (signature.charAt(offset) == '(') {
            int offset4 = offset + 1;
            while (signature.charAt(offset4) != ')') {
                offset4 = parseType(signature, offset4, signatureVistor.visitParameterType());
            }
            int offset5 = parseType(signature, offset4 + 1, signatureVistor.visitReturnType());
            while (offset5 < length) {
                offset5 = parseType(signature, offset5 + 1, signatureVistor.visitExceptionType());
            }
            return;
        }
        int offset6 = parseType(signature, offset, signatureVistor.visitSuperclass());
        while (offset6 < length) {
            offset6 = parseType(signature, offset6, signatureVistor.visitInterface());
        }
    }

    public void acceptType(SignatureVisitor signatureVisitor) {
        parseType(this.signatureValue, 0, signatureVisitor);
    }

    private static int parseType(String signature, int startOffset, SignatureVisitor signatureVisitor) {
        int offset = startOffset + 1;
        char currentChar = signature.charAt(startOffset);
        if (currentChar != 'F') {
            if (currentChar == 'L') {
                int start = offset;
                boolean visited = false;
                boolean inner = false;
                while (true) {
                    int offset2 = offset + 1;
                    char currentChar2 = signature.charAt(offset);
                    if (currentChar2 == '.' || currentChar2 == ';') {
                        if (!visited) {
                            String name = signature.substring(start, offset2 - 1);
                            if (inner) {
                                signatureVisitor.visitInnerClassType(name);
                            } else {
                                signatureVisitor.visitClassType(name);
                            }
                        }
                        if (currentChar2 == ';') {
                            signatureVisitor.visitEnd();
                            return offset2;
                        }
                        start = offset2;
                        visited = false;
                        inner = true;
                        offset = offset2;
                    } else if (currentChar2 != '<') {
                        offset = offset2;
                    } else {
                        String name2 = signature.substring(start, offset2 - 1);
                        if (inner) {
                            signatureVisitor.visitInnerClassType(name2);
                        } else {
                            signatureVisitor.visitClassType(name2);
                        }
                        visited = true;
                        while (true) {
                            char currentChar3 = signature.charAt(offset2);
                            if (currentChar3 == '>') {
                                break;
                            }
                            if (currentChar3 == '*') {
                                offset2++;
                                signatureVisitor.visitTypeArgument();
                            } else if (currentChar3 == '+' || currentChar3 == '-') {
                                offset2 = parseType(signature, offset2 + 1, signatureVisitor.visitTypeArgument(currentChar3));
                            } else {
                                offset2 = parseType(signature, offset2, signatureVisitor.visitTypeArgument(SignatureVisitor.INSTANCEOF));
                            }
                        }
                        offset = offset2;
                    }
                }
            } else if (currentChar != 'V' && currentChar != 'I' && currentChar != 'J' && currentChar != 'S') {
                if (currentChar == 'T') {
                    int endOffset = signature.indexOf(59, offset);
                    signatureVisitor.visitTypeVariable(signature.substring(offset, endOffset));
                    return endOffset + 1;
                }
                if (currentChar != 'Z') {
                    if (currentChar == '[') {
                        return parseType(signature, offset, signatureVisitor.visitArrayType());
                    }
                    switch (currentChar) {
                        case 'B':
                        case 'C':
                        case 'D':
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
        }
        signatureVisitor.visitBaseType(currentChar);
        return offset;
    }
}
