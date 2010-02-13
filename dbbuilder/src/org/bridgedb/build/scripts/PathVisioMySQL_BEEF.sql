## Beef up a MySQL Database for hosted access
## A.Pico 20100209

## Beef up 'datanode'
## Use 'Name' for GO-Slim additions
INSERT INTO datanode
SELECT XXXXXX_CS_YYYYYY.gene.Name, XXXXXX_CS_YYYYYY.gene.Code
FROM XXXXXX_CS_YYYYYY.gene
WHERE XXXXXX_CS_YYYYYY.gene.Name != ''
AND XXXXXX_CS_YYYYYY.gene.Code IN ("Tb", "Tc", "Tm");

## Beef up 'link'
## Use 'gene.Name' for GO-Slim additions
INSERT INTO link
SELECT DISTINCT id_left, code_left, Name, code_right
FROM XXXXXX_CS_YYYYYY.link left join XXXXXX_CS_YYYYYY.gene on XXXXXX_CS_YYYYYY.link.id_right = XXXXXX_CS_YYYYYY.gene.id
WHERE XXXXXX_CS_YYYYYY.link.Code_Right IN ("Tb", "Tc", "Tm");


