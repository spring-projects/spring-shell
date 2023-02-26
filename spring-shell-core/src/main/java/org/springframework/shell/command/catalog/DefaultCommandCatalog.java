package org.springframework.shell.command.catalog;

import org.springframework.shell.command.CommandAlias;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.CommandResolver;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.context.ShellContext;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default implementation of a {@link CommandCatalog}.
 */
class DefaultCommandCatalog implements CommandCatalog {

    private final Map<String, CommandRegistration> commandRegistrations = new HashMap<>();
    private final Collection<CommandResolver> resolvers = new ArrayList<>();
    private final ShellContext shellContext;

    DefaultCommandCatalog(Collection<CommandResolver> resolvers, ShellContext shellContext) {
        this.shellContext = shellContext;
        if (resolvers != null) {
            this.resolvers.addAll(resolvers);
        }
    }

    @Override
    public void register(CommandRegistration... registration) {
        for (CommandRegistration r : registration) {
            String commandName = r.getCommand();
            commandRegistrations.put(commandName, r);
            for (CommandAlias a : r.getAliases()) {
                commandRegistrations.put(a.getCommand(), r);
            }
        }
    }

    @Override
    public void unregister(CommandRegistration... registration) {
        for (CommandRegistration r : registration) {
            String commandName = r.getCommand();
            commandRegistrations.remove(commandName);
            for (CommandAlias a : r.getAliases()) {
                commandRegistrations.remove(a.getCommand());
            }
        }
    }

    @Override
    public void unregister(String... commandName) {
        for (String n : commandName) {
            commandRegistrations.remove(n);
        }
    }

    @Override
    public Map<String, CommandRegistration> getRegistrations() {
        Map<String, CommandRegistration> regs = new HashMap<>(commandRegistrations);
        for (CommandResolver resolver : resolvers) {
            resolver.resolve().forEach(r -> regs.put(r.getCommand(), r));
        }
        return regs.entrySet().stream()
                .filter(filterByInteractionMode(shellContext))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Filter registration entries by currently set mode. Having it set to ALL or null
     * effectively disables filtering as as we only care if mode is set to interactive
     * or non-interactive.
     */
    private static Predicate<Map.Entry<String, CommandRegistration>> filterByInteractionMode(ShellContext shellContext) {
        return e -> {
            InteractionMode mim = e.getValue().getInteractionMode();
            InteractionMode cim = shellContext != null ? shellContext.getInteractionMode() : InteractionMode.ALL;
            if (mim == null || cim == null || mim == InteractionMode.ALL) {
                return true;
            }
            else if (mim == InteractionMode.INTERACTIVE) {
                return cim == InteractionMode.INTERACTIVE || cim == InteractionMode.ALL;
            }
            else if (mim == InteractionMode.NONINTERACTIVE) {
                return cim == InteractionMode.NONINTERACTIVE || cim == InteractionMode.ALL;
            }
            return true;
        };
    }

}
