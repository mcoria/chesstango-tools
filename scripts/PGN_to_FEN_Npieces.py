import chess.pgn

# Define a constant for the target number of pieces on the board
TARGET_PIECES = 6

# Main function to process PGN file and output FEN strings
# at the point where the number of pieces reaches TARGET_PIECES
# being at most MAX_DEPTH moves before that point.
def main():
    # Abre el archivo PGN que contiene los games que comienzan con al menos TARGET_PIECES + 1
    pgn = open(r"C:\java\projects\chess\chess-utils\testing\PGN\full\LumbrasGigaBase\OverTheBoard\LumbrasGigaBase_OTB_2025_no_clock.pgn", encoding="utf-8")
    while game := chess.pgn.read_game(pgn):
        if game is None:
            break
        board = game.board()
        if countPieces(board) == TARGET_PIECES:
            print(board.fen())
        elif countPieces(board) > TARGET_PIECES:
            for move in game.mainline_moves():
                board.push(move)
                if countPieces(board) == TARGET_PIECES:
                    print(board.fen())
                    break

# Cuenta la cantidad de piezas en el tablero
def countPieces(board: chess.Board) -> int:
    piece_count = 0
    for square in chess.SQUARES:
        if board.piece_at(square) is not None:
            piece_count += 1
    return piece_count

if __name__ == "__main__":
    main()
