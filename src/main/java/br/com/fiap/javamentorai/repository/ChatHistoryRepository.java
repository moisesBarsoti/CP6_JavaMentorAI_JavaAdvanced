package br.com.fiap.javamentorai.repository;

import br.com.fiap.javamentorai.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findAllByOrderByCreatedAtDesc();

}