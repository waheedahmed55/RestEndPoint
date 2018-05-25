--STATEMENT createCard
INSERT INTO card (
  card_name,
  card_number,
  card_image,
  card_image_mime_type,
  card_type,
  card_description
) VALUES(
  :card_name,
  :card_number,
  :card_image,
  :card_image_mime_type,
  :card_type,
  :card_description
);

--STATEMENT readCard
SELECT * FROM card WHERE id = :id;

--STATEMENT updateCard
UPDATE card 
SET card_name = :card_name,
	card_number = :card_number,
	card_image = :card_image,
	card_image_mime_type = :card_image_mime_type,
  	card_type = :card_type,
  	card_description = :card_description
WHERE id = :id;

--STATEMENT deleteCard
DELETE FROM card WHERE id = :id;
