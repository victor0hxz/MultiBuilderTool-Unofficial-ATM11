"""Generate the Naquadah MultiBuilder template directly from the Extras validator grid."""
import gzip
import json
import struct
from pathlib import Path

GRID = [
    [0,0,0,1,1,1,0,0,0], [0,1,1,2,2,2,1,1,0], [0,1,2,2,2,2,2,1,0],
    [1,2,2,2,2,2,2,2,1], [1,2,2,2,2,2,2,2,1], [1,2,2,2,2,2,2,2,1],
    [0,1,2,2,2,2,2,1,0], [0,1,1,2,2,2,1,1,0], [0,0,0,1,1,1,0,0,0]
]

def block(x,y,z):
    walls = []
    if x in (0,8): walls.append(GRID[y][z])
    if y in (0,8): walls.append(GRID[x][z])
    if z in (0,8): walls.append(GRID[x][y])
    if not any(walls): return 0  # air, including the empty interior
    if (x,y,z) == (4,8,4): return 2
    if (x,y,z) == (4,4,0): return 3
    if (x,y,z) == (3,4,8): return 4
    if (x,y,z) == (5,4,8): return 5
    return 1

def string(value):
    data = value.encode('utf-8')
    return struct.pack('>H', len(data)) + data

def integer(value): return struct.pack('>i', value)
def tag(kind,name,data): return bytes([kind]) + string(name) + data
def compound(*children): return b''.join(children) + b'\0'
def listing(kind,entries): return bytes([kind]) + integer(len(entries)) + b''.join(entries)

names = ['minecraft:air'] + ['mekanism_extras:' + name for name in [
    'naquadah_reactor_casing','naquadah_reactor_controller','lead_coated_laser_focus_matrix',
    'naquadah_reactor_port','naquadah_reactor_port']]
palette = []
for i,name in enumerate(names):
    properties = []
    if i in (3,4,5): properties.append(tag(8,'facing',string('north' if i == 3 else 'south')))
    palette.append(compound(tag(8,'Name',string(name)), tag(10,'Properties',compound(*properties))))
entries = []
for x in range(9):
    for y in range(9):
        for z in range(9):
            entries.append(compound(tag(9,'pos',listing(3,[integer(x),integer(y),integer(z)])),tag(3,'state',integer(block(x,y,z)))))
root = tag(10,'',compound(tag(3,'DataVersion',integer(0)),tag(9,'size',listing(3,[integer(9)]*3)),
    tag(9,'palette',listing(10,palette)),tag(9,'blocks',listing(10,entries)),tag(9,'entities',listing(10,[]))))
dest = Path(__file__).resolve().parents[1] / 'src/main/resources/data/mbtool/mbtool_structures/naquadah_reactor.nbt'
dest.parent.mkdir(parents=True,exist_ok=True)
dest.write_bytes(gzip.compress(root,mtime=0))
counts = {name: sum(block(x,y,z) == i for x in range(9) for y in range(9) for z in range(9)) for i,name in enumerate(names[:4])}
print('Template generated:', dest, counts, 'ports=2')
