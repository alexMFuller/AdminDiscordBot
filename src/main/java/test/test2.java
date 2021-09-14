package test;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.VoiceChannel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test2 {

        public static void main(String[] args) {
            GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                    .build()
                    .login()
                    .block();

                client.getEventDispatcher().on(MessageCreateEvent.class)
                    // 3.1 Message.getContent() is a String
                    .flatMap(event -> Mono.just(event.getMessage().getContent())
                            .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                    // We will be using ! as our "prefix" to any command in the system.
                                    .filter(entry -> content.startsWith('!' + entry.getKey()))
                                    .flatMap(entry -> entry.getValue().execute(event))
                                    .next()))
                    .subscribe();


                client.onDisconnect().block();


        }

        public Role findRole(Member member, String name) {
            List<Role> roles = (List<Role>) member.getRoles();
            return roles.stream()
                    .filter(role -> role.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }

    private static final Map<String, Commands> commands = new HashMap<>();

    static {

        commands.put("CreateRole", event -> Mono.just(event.getMessage())

                .flatMap(Message::getGuild)
                .flatMap(role -> role.createRole(spec -> {
                    spec.setName(event.getMessage().getContent().trim().split("\\s")[1]);
                })));

        commands.put("CreateChannel", event -> Mono.just(event.getMessage())

                .flatMap(Message::getGuild)
                .flatMap(guild -> guild.createVoiceChannel(spec -> {
                    spec.setName(event.getMessage().getContent().trim().split("\\s")[1]);
                })));
        commands.put("DeleteServers", event -> Mono.just(event.getMessage())
                .flatMap(Message::getGuild)
                .flatMap(Guild-> Guild.getChannels().blockLast().delete())

        );
    }

    }



