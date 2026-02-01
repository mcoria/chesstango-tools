import chess
import chess.polyglot
import chess.pgn

pgn_file = open("output.pgn", "a")

board = chess.Board()

#board.set_fen("r6r/pN1nkpp1/2p1pn1p/7P/3q4/1Q6/PPPN1PP1/R3K2R w KQ - 0 18")
print(f"Setting FEN...")

#reader =  chess.polyglot.open_reader("C:\\java\\projects\\chess\\chess-utils\\books\\openings\\Perfect_2023\\BIN\\Perfect2023.bin")
reader =  chess.polyglot.open_reader("C:\\java\\projects\\chess\\chess-utils\\tools\\bin\\chesstango.bin")


def walk():
    leaf = True
    for entry in reader.find_all(board):
        if entry.move in board.legal_moves:
            board.push(entry.move)
            if board.is_repetition(2):
                board.pop()
                continue
            leaf = False
            walk()
            board.pop()
    if leaf:
        game = chess.pgn.Game.from_board(board)
        #pgn_string = str(game)
        print(game, file=pgn_file)
        print("", file=pgn_file)
    return

walk()

reader.close()

pgn_file.close()


