ALTER TABLE venue
MODIFY COLUMN time_zone ENUM('PST', 'EST', 'CST', 'MST', 'GMT', 'UTC');
