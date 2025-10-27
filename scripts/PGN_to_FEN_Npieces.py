import chess.pgn

# Define a constant for the target number of pieces on the board
TARGET_PIECES = 5

# Define a constant for the maximum depth reach the target number of pieces
MAX_DEPTH  = 6


# Cuenta la cantidad de piezas en el tablero
def countPieces(board: chess.Board) -> int:
    piece_count = 0
    for square in chess.SQUARES:
        if board.piece_at(square) is not None:
            piece_count += 1
    return piece_count

# Determina la profundidad en la que el juego alcanza el número objetivo de piezas
def depthToTargetPieces(game: chess.pgn.Game) -> int:
    moveCounter = 0
    board = game.board()
    for move in game.mainline_moves():
        board.push(move)
        moveCounter += 1
        if countPieces(board) == TARGET_PIECES:
            break
    return moveCounter

# Abre el archivo PGN que contiene los juegos con al menos TARGET_PIECES + 1
pgn = open(r"C:\java\projects\chess\chess-utils\testing\matches\LumbrasGigaBase\LumbrasGigaBase_OTB_2025_6_pieces_finalLessThan6_whiteWins.pgn")

while game := chess.pgn.read_game(pgn):
    if game is None:
        break
    depth = depthToTargetPieces(game)
    if depth > 0:
        board = game.board()
        if depth > MAX_DEPTH:
            for move in game.mainline_moves():
                board.push(move)
                depth -= 1
                if depth == MAX_DEPTH:
                    break
        print(board.fen())

