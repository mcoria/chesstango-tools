import subprocess
from pathlib import Path

fens = []

# Read FEN strings from the input file and store them in a list
inputFilePath = Path(r"C:\java\projects\chess\chess-utils\testing\matches\LumbrasGigaBase\LumbrasGigaBase_OTB_2025_6_pieces_finalLessThan6_draw.fen")
with inputFilePath.open("r", encoding="utf-8") as input_file:
    for fen in input_file: 
        fens.append(fen.strip())

# Prepare the command to call fathom-pgn.exe
# El comando fathom-pgn.exe (version custom) genera un archivo PGN a partir de una cadena FEN usando la tabla de finales Syzygy
cmd = [r"C:\java\projects\chess\chess-utils\books\syzygy\Fathom\src\apps\fathom-pgn.exe", r"--path=E:\syzygy", ""]

# Open the output PGN file and process each FEN string
out_path = Path(r"C:\java\projects\chess\chess-utils\testing\matches\LumbrasGigaBase\LumbrasGigaBase_OTB_2025_6_pieces_finalLessThan6_draw.pgn")
with out_path.open("a") as out_file:
    for fen in fens:
        print("Processing: " + fen)
        cmd[2] = fen
        result = subprocess.run(cmd, stdout=out_file, stderr=subprocess.STDOUT, check=True)
        out_file.write('\n')
        out_file.flush()

