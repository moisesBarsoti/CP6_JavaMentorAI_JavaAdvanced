
const chatForm = document.getElementById("chatForm");
const messageInput = document.getElementById("messageInput");
const sendButton = document.getElementById("sendButton");
const chatMessages = document.getElementById("chatMessages");
const statusMessage = document.getElementById("statusMessage");
const characterCount = document.getElementById("characterCount");
const historyButton = document.getElementById("historyButton");
const historyList = document.getElementById("historyList");

const API_CHAT = "/api/chat";
const API_HISTORY = "/api/historico";

let isLoading = false;

// Atualizar contador de caracteres

messageInput.addEventListener("input", () => {
    characterCount.textContent = `${messageInput.value.length} / 2000`;
});

// Exibir mensagens no chat

function addMessage(content, type) {
    const welcomeMessage = chatMessages.querySelector(".welcome-message");

    if (welcomeMessage) {
        welcomeMessage.remove();
    }

    const message = document.createElement("div");
    message.classList.add("message", type);

    const label = document.createElement("span");
    label.classList.add("message-label");

    label.textContent = type === "user"
        ? "Você"
        : "JavaMentor AI";

    const messageContent = document.createElement("div");
    messageContent.classList.add("message-content");
    messageContent.textContent = content;

    message.appendChild(label);
    message.appendChild(messageContent);

    chatMessages.appendChild(message);

    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Exibir mensagens de status

function setStatus(message, isError = false) {
    statusMessage.textContent = message;
    statusMessage.classList.toggle("error-message", isError);
}

// Controlar estado de carregamento

function setLoading(loading) {
    isLoading = loading;

    sendButton.disabled = loading;
    messageInput.disabled = loading;

    sendButton.textContent = loading
        ? "Processando..."
        : "Enviar pergunta";
}

// Interpretar erros retornados pela API

async function getErrorMessage(response) {
    try {
        const error = await response.json();

        return error.detail
            || error.message
            || `Erro HTTP ${response.status}`;
    } catch {
        return `Não foi possível processar a requisição. HTTP ${response.status}`;
    }
}

// Enviar pergunta ao Spring Boot

chatForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    if (isLoading) {
        return;
    }

    const question = messageInput.value.trim();

    if (!question) {
        setStatus("Digite uma pergunta antes de enviar.", true);
        messageInput.focus();
        return;
    }

    if (question.length > 2000) {
        setStatus("A mensagem deve ter no máximo 2000 caracteres.", true);
        return;
    }

    addMessage(question, "user");

    setLoading(true);
    setStatus("O JavaMentor AI está preparando sua resposta...");

    try {
        const response = await fetch(API_CHAT, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                mensagem: question
            })
        });

        if (!response.ok) {
            const errorMessage = await getErrorMessage(response);
            throw new Error(errorMessage);
        }

        const data = await response.json();

        addMessage(data.resposta, "assistant");

        messageInput.value = "";
        characterCount.textContent = "0 / 2000";

        setStatus("Resposta gerada com sucesso!");

        await loadHistory();

    } catch (error) {
        setStatus(
            error.message || "Não foi possível comunicar com a API.",
            true
        );
    } finally {
        setLoading(false);
        messageInput.focus();
    }
});

// Formatar data do histórico

function formatDate(dateValue) {
    if (!dateValue) {
        return "Data indisponível";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return dateValue;
    }

    return date.toLocaleString("pt-BR");
}

// Exibir conversa selecionada do histórico

function showHistoryConversation(conversation) {
    chatMessages.replaceChildren();

    addMessage(conversation.pergunta, "user");
    addMessage(conversation.resposta, "assistant");

    setStatus("");
}

// Consultar histórico no PostgreSQL

async function loadHistory() {
    historyList.replaceChildren();

    const loadingMessage = document.createElement("p");
    loadingMessage.textContent = "Carregando histórico...";
    historyList.appendChild(loadingMessage);

    try {
        const response = await fetch(API_HISTORY, {
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            const errorMessage = await getErrorMessage(response);
            throw new Error(errorMessage);
        }

        const history = await response.json();

        historyList.replaceChildren();

        if (!history.length) {
            const emptyMessage = document.createElement("p");
            emptyMessage.textContent = "Nenhuma conversa encontrada.";

            historyList.appendChild(emptyMessage);
            return;
        }

        history.forEach((conversation) => {
            const item = document.createElement("button");

            item.type = "button";
            item.classList.add("history-item");

            const title = document.createElement("strong");
            title.textContent = conversation.pergunta;

            const date = document.createElement("span");
            date.textContent = formatDate(conversation.dataHora);

            item.appendChild(title);
            item.appendChild(date);

            item.addEventListener("click", () => {
                showHistoryConversation(conversation);
            });

            historyList.appendChild(item);
        });

    } catch (error) {
        historyList.replaceChildren();

        const errorMessage = document.createElement("p");
        errorMessage.textContent = "Não foi possível carregar o histórico.";
        errorMessage.classList.add("error-message");

        historyList.appendChild(errorMessage);

        console.error("Erro ao consultar histórico:", error);
    }
}

// Atualizar histórico manualmente

historyButton.addEventListener("click", loadHistory);

// Carregar histórico ao iniciar a página

loadHistory();
