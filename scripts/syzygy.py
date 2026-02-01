from pathlib import Path
import chess
import chess.syzygy

white_wins = []
black_wins = []
draws = []

inputFilePath = Path(r"C:\java\projects\chess\chess-utils\testing\PGN\full\LumbrasGigaBase\OverTheBoard\LumbrasGigaBase_OTB_2025_6_pieces.fen")
with chess.syzygy.open_tablebase(r"E:\syzygy") as tablebase:
        with inputFilePath.open("r", encoding="utf-8") as input_file:
            for fen in input_file: 
                board = chess.Board(fen)
                result = tablebase.probe_wdl(board)
                if result == 2 and board.turn == chess.WHITE or result == -2 and board.turn == chess.BLACK:
                    white_wins.append(fen)
                elif result == 2 and board.turn == chess.BLACK or result == -2 and board.turn == chess.WHITE:
                    black_wins.append(fen)
                elif result == 0:
                    draws.append(fen)


outputFilePath = Path(r"C:\java\projects\chess\chess-utils\testing\PGN\full\LumbrasGigaBase\OverTheBoard\LumbrasGigaBase_OTB_2025_6_pieces_results.txt")
with outputFilePath.open("w", encoding="utf-8") as output_file:
    output_file.write("White Wins:\n")
    for fen in white_wins:
        output_file.write(fen)
    output_file.write("\nBlack Wins:\n")
    for fen in black_wins:
        output_file.write(fen)
    output_file.write("\nDraws:\n")
    for fen in draws:
        output_file.write(fen)