-- Create INDEXES
CREATE INDEX i_codeLeft ON link(codeLeft);
CREATE INDEX i_right ON link(idRight, codeRight);
CREATE INDEX i_dnId ON datanode(id);
CREATE INDEX i_dnCode ON datanode(code);
CREATE INDEX i_attrCode ON attribute(code);
CREATE INDEX i_attrValue ON attribute(attrValue);
CREATE INDEX i_attrId ON attribute(id);
CREATE INDEX i_attrName ON attribute(attrName);
CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE('APP', 'LINK', 1);
CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE('APP', 'DATANODE', 1);
CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE('APP', 'ATTRIBUTE', 1);

