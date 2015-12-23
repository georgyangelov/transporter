package net.gangelov.args;

import java.util.*;
import java.util.stream.Collectors;

public class ArgumentParser {
    public final Map<String, String> arguments = new HashMap<>();
    public final Set<String> flags = new HashSet<>();


    private String[] allowedFlags, allowedArguments;

    public ArgumentParser(String[] allowedFlags, String[] allowedArguments) {
        this.allowedFlags = allowedFlags;
        this.allowedArguments = allowedArguments;
    }

    public void parse(String[] args) throws ArgumentParserException {
        Set<String> allowedFlagNames = Arrays.stream(allowedFlags).collect(Collectors.toSet());
        Set<String> allowedArgumentNames = Arrays.stream(allowedArguments).collect(Collectors.toSet());

        for (int i = 0; i < args.length; i++) {
            String name = args[i].replaceFirst("^--?", "");

            if (allowedFlagNames.contains(name)) {
                flags.add(name);
            } else if (allowedArgumentNames.contains(name)) {
                if (i == args.length - 1) {
                    throw new ArgumentParserException("No value for argument " + name);
                }

                arguments.put(name, args[++i]);
            } else {
                throw new ArgumentParserException("Unsupported flag or argument " + name);
            }
        }
    }

    public class ArgumentParserException extends Exception {
        public ArgumentParserException(String message) {
            super(message);
        }
    }
}
