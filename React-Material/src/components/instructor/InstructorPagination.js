import React from 'react';
import { Box, Pagination, PaginationItem } from '@mui/material';

const InstructorPagination = ({ totalPages, currentPage, handlePageChange }) => {
  return (
    <Box display="flex" justifyContent="center" mt={3}>
      <Pagination
        count={totalPages}
        page={currentPage}
        onChange={(event, value) => {
          handlePageChange(event, value);
        }}
        variant="outlined"
        renderItem={(item) => (
          <PaginationItem
            {...item}
            disabled={
              (item.type === 'next' && currentPage >= totalPages) ||
              (item.type === 'previous' && currentPage <= 1)
            }
          />
        )}
      />
    </Box>
  );
};

export default InstructorPagination;