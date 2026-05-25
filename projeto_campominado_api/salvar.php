<?php
require_once __DIR__ . '/config.php';

try {
    $input = json_decode(file_get_contents('php://input'), true);

    if (!is_array($input)) {
        jsonResponse(false, 'JSON inválido', null, 400);
    }

    $nome = trim($input['nome_jogador'] ?? '');
    $pontuacao = (int) ($input['pontuacao'] ?? 0);

    if ($nome === '') {
        jsonResponse(false, 'Nome do jogador é obrigatório', null, 400);
    }

    if ($pontuacao < 0) {
        jsonResponse(false, 'Pontuação inválida', null, 400);
    }

    $pdo = getConnection();
    $stmt = $pdo->prepare(
        'INSERT INTO partidas (nome_jogador, pontuacao) VALUES (:nome, :pontuacao)'
    );
    $stmt->execute([
        ':nome' => $nome,
        ':pontuacao' => $pontuacao,
    ]);

    jsonResponse(true, 'Partida salva com sucesso', [
        'id' => (int) $pdo->lastInsertId(),
        'nome_jogador' => $nome,
        'pontuacao' => $pontuacao,
    ]);
} catch (PDOException $e) {
    jsonResponse(false, 'Erro de banco de dados: ' . $e->getMessage(), null, 500);
} catch (Exception $e) {
    jsonResponse(false, $e->getMessage(), null, 500);
}
