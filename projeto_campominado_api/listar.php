<?php
require_once __DIR__ . '/config.php';

try {
    $pdo = getConnection();
    $stmt = $pdo->query(
        'SELECT id, nome_jogador, pontuacao, data_partida
         FROM partidas
         ORDER BY data_partida DESC'
    );
    $partidas = $stmt->fetchAll();
    jsonResponse(true, 'Partidas listadas com sucesso', $partidas);
} catch (PDOException $e) {
    jsonResponse(false, 'Erro de banco de dados: ' . $e->getMessage(), null, 500);
} catch (Exception $e) {
    jsonResponse(false, $e->getMessage(), null, 500);
}
