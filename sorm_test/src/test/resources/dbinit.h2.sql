CREATE TABLE object (
  id INTEGER PRIMARY KEY,
  type VARCHAR(32) NOT NULL
);

CREATE SEQUENCE object_id_seq;
ALTER TABLE object ALTER COLUMN id SET DEFAULT nextval('object_id_seq');

CREATE INDEX object_type_idx ON object (type);


CREATE TABLE person (
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  mother INTEGER,
  father INTEGER,
  sex TEXT NOT NULL,
  dob TEXT NOT NULL,
  spouse INTEGER,
  height INTEGER NOT NULL,
  weight DOUBLE NOT NULL,
  hair_color CHAR(2) NOT NULL,
  eye_color CHAR(2) NOT NULL,
  hair_color_alt CHAR(2),

  FOREIGN KEY (id) REFERENCES object (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (mother) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (father) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (spouse) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE
);


CREATE TABLE friendmap (
  person_id1 INTEGER NOT NULL,
  person_id2 INTEGER NOT NULL,

  FOREIGN KEY (person_id1) REFERENCES person (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (person_id2) REFERENCES person (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX friendmap_person_id1_idx ON friendmap (person_id1);
CREATE INDEX friendmap_person_id2_idx ON friendmap (person_id2);
