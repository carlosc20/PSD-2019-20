-module(frontend).

-include("protos/protos.hrl").

%% API exports
-export([main/1]).


%%====================================================================
%% API functions
%%====================================================================

start_worker(Identity, Port) ->
    {ok, Socket} = chumak:socket(dealer, Identity),
    %rep no negociador faz bind ou ao contrário?
    {ok, _PeerPid} = chumak:connect(Socket, tcp, "localhost", Port),
    worker_loop(Socket, Identity).


worker_loop(Socket, Identity) ->
    %uma de cada vez
    receive
        {Identity, Message, Pid} ->
            %novo processo para não empancar tudo à espera da resposta
            spawn(fun() -> chumak:send(Socket, [Identity, Message]),
                            {ok, Multipart} = chumak:recv_multipart(Socket),
                            %Identidade já está no execute_job()
                            Pid ! {answer, Multipart} end)
    end.

%encarrega-se de devolver respostas aos clientes
answer_loop(Socket) -> 
    receive
        {Identity, Message} ->
            ok = chumak:send_multipart(Socket, [Identity, <<>>, Message])
    end.

%%processo que se encarrega de receber pedidos de clientes
request_loop(Socket, Parent, Workers) ->
    {ok, [Identity, Message]} = chumak:recv_multipart(Socket),
    %%gera número aleatório
    N = rand:uniform(4),
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
    Worker ! {Identity, Message, self()},
    receive
        {answer, Message} ->
            %devolve resposta à main que está a correr answer_loop
            Parent ! {Identity, Message}
    end.

main(Args) ->
    io:format("~p~n", [Args]),
    application:ensure_started(chumak),
    {ok, Socket} = chumak:socket(router),
    {ok, _BindPid} = chumak:bind(Socket, tcp, "localhost", 5555),

    %lista de trabalhadores (pid). Trabalhadores para enviar/receber de um negociador, cada um está encarregue de um negociador 
    Workers=#{"A" => spawn(fun() -> start_worker("A", 5556) end),
              "B" => spawn(fun() -> start_worker("B", 5557) end),
              "C" => spawn(fun() -> start_worker("C", 5558) end)
    },
    Parent = self(),
    spawn(fun() -> request_loop(Socket, Parent, Workers) end),
    answer_loop(Socket).

%%====================================================================
%% Internal functions
%%====================================================================