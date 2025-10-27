import subprocess
from pathlib import Path

fens = []

inputFilePath = Path(r"C:\java\projects\chess\chess-utils\testing\matches\LumbrasGigaBase\LumbrasGigaBase_OTB_2025_6_pieces_finalLessThan6_whiteWins.fen")
with inputFilePath.open("r", encoding="utf-8") as input_file:
    for fen in input_file: 
        fens.append(fen.strip())

cmd = [r"C:\java\projects\chess\chess-utils\books\syzygy\Fathom\src\apps\fathom-pgn.exe", r"--path=E:\syzygy", ""]

out_path = Path(r"C:\java\projects\chess\chess-utils\testing\matches\LumbrasGigaBase\LumbrasGigaBase_OTB_2025_6_pieces_finalLessThan6_whiteWins.pgn")
with out_path.open("a") as out_file:
    for fen in fens:
        print("Processing:" + fen)
        cmd[2] = fen
        result = subprocess.run(cmd, stdout=out_file, stderr=subprocess.STDOUT, check=True)
        out_file.write('\n')
        out_file.flush()

