-module(frontend).

-include("protos/protos.hrl").

%% API exports
-export([main/1]).


%%====================================================================
%% API functions
%%====================================================================

start_worker(Identity, DestIdentity, Port) ->
    {ok, Socket} = chumak:socket(dealer, Identity),
    {ok, _PeerPid} = chumak:bind(Socket, tcp, "localhost", Port),
    worker_loop(Socket, Identity, DestIdentity).


worker_loop(Socket, Identity, DestIdentity) ->
    %uma de cada vez
    Iden = list_to_binary(Identity),
    receive
        {Message, Pid} ->
            io:format("~p~p~n", [Message, Pid]),
            %novo processo para não empancar tudo à espera da resposta
            spawn_link(fun() -> chumak:send_multipart(Socket, [DestIdentity, Iden, <<>>, Message]),
                            {ok, Multipart} = chumak:recv_multipart(Socket),
                            %Identidade já está no execute_job()
                            Pid ! {answer, Multipart} end);
        _ -> io:format("~p~n", ["invalido"])
    end,
    worker_loop(Socket, Identity, DestIdentity).

%encarrega-se de devolver respostas aos clientes
answer_loop(Socket) -> 
    receive
        {Identity, [_, _, <<>>, Message]} ->
            ok = chumak:send_multipart(Socket, [Identity, <<>>, Message])
    end,
    answer_loop(Socket).

%%processo que se encarrega de receber pedidos de clientes
request_loop(Socket, Parent, Workers) ->
    {ok, [Identity, <<>>, Message]} = chumak:recv_multipart(Socket),
    io:format("~p~n", [Message]),
    %%gera número aleatório
    N = rand:uniform(3),
    case N of
        1 -> Worker = maps:get("A", Workers);
        2 -> Worker = maps:get("B", Workers);
        3 -> Worker = maps:get("C", Workers)
    end,
    %%caso necessário ver o que tem a msg
    spawn(fun() -> execute_job(Parent, Identity, Message, Worker) end),
    request_loop(Socket, Parent, Workers).

%%processo que se encarrega de pedir a um tabalhador que envia ao negociador algo
execute_job(Parent, Identity, Message, Worker) ->
    %envia pedido ao trabalhador
    io:format("~p~n", [Worker]),
    Worker ! {Message, self()},
    receive
        {answer, Recv} ->
            io:format("~p~n", [Recv]),
            %devolve resposta à main que está a correr answer_loop
            Parent ! {Identity, Recv}
    end.

main(Args) ->
    io:format("~p~n", [Args]),
    application:ensure_started(chumak),
    {ok, Socket} = chumak:socket(router),
    {ok, _BindPid} = chumak:bind(Socket, tcp, "localhost", 5555),

    %lista de trabalhadores (pid). Trabalhadores para enviar/receber de um negociador, cada um está encarregue de um negociador 
    Workers=#{"A" => spawn(fun() -> start_worker("A", <<"X">>, 5556) end),
              "B" => spawn(fun() -> start_worker("B", <<"Y">>, 5557) end),
              "C" => spawn(fun() -> start_worker("C", <<"Z">>, 5558) end)
    },
    Parent = self(),
    spawn(fun() -> request_loop(Socket, Parent, Workers) end),
    answer_loop(Socket).

%%====================================================================
%% Internal functions
%%====================================================================