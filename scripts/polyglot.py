import chess
import chess.polyglot

board = chess.Board()

board.set_fen("r6r/pN1nkpp1/2p1pn1p/7P/3q4/1Q6/PPPN1PP1/R3K2R w KQ - 0 18")

with chess.polyglot.open_reader("C:/java/projects/chess/chess-utils/books/openings/polyglot-collection/komodo.bin") as reader:
   for entry in reader.find_all(board):
       print(entry.move, entry.weight, entry.learn)
