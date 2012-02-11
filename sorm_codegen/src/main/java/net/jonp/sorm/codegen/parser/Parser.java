package net.jonp.sorm.codegen.parser;

import java.io.IOException;
import java.io.Reader;

import net.jonp.sorm.codegen.model.Sorm;

/**
 * Interface for parsers that generate {@link Sorm} code generation
 * descriptions.
 */
public interface Parser
{
    /**
     * Parse the given {@link Reader} input and generate a {@link Sorm} code
     * generation description.
     * 
     * @param in The {@link Reader} providing input.
     * @return The {@link Sorm} description.
     * @throws BadInputException If the input was invalid in any way.
     * @throws IOException If there was a problem reading the input.
     */
    public Sorm parseSorm(Reader in)
        throws BadInputException, IOException;
}
