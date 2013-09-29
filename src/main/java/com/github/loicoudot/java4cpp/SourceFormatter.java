package com.github.loicoudot.java4cpp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public final class SourceFormatter implements TemplateDirectiveModel {

    @Override
    @SuppressWarnings("rawtypes")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateModelException("SourceFormatter doesn't allow parameters.");
        }
        if (loopVars.length != 0) {
            throw new TemplateModelException("SourceFormatter doesn't allow loop variables.");
        }
        if (body != null) {
            body.render(new CppSourceFormatterWriter(env.getOut()));
        } else {
            throw new RuntimeException("missing body");
        }
    }

    private static class CppSourceFormatterWriter extends Writer {

        enum State {
            GOT_CR, GOT2_CR, FORMATTING
        }

        private final Writer out;
        private State state = State.GOT_CR;
        private String indent = "";

        CppSourceFormatterWriter(Writer out) {
            this.out = out;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            for (int i = 0; i < len; ++i) {
                addChar(cbuf[off + i]);
            }
        }

        private void addChar(char c) throws IOException {
            switch (state) {
            case GOT_CR:
            case GOT2_CR:
                if (c == '\n') {
                    if (state == State.GOT_CR) {
                        out.write(c);
                    }
                    state = State.GOT2_CR;
                    return;
                }
                if (Character.isWhitespace(c)) {
                    return;
                }
                if (c == '}') {
                    indent = indent.substring(3);
                }
                out.write(indent);
                out.write(c);
                if (c == '{') {
                    indent = indent + "   ";
                }
                state = State.FORMATTING;
                break;
            case FORMATTING:
                if (c == '{') {
                    indent = indent + "   ";
                }
                if (c == '}') {
                    indent = indent.substring(3);
                }
                if (c == '\n') {
                    state = State.GOT_CR;
                }
                out.write(c);
                break;
            }
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

}