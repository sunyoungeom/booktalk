//package com.sunyoungeom.booktalk.repository;
//
//import com.sunyoungeom.booktalk.domain.ReviewLikes;
//
//import java.util.*;
//
////@Repository
//public class ReviewLikesRepositoryMemory implements ReviewLikesRepository {
//
//    private static Map<Long, ReviewLikes> store = new HashMap<>();
//    private static long sequence = 0L;
//
//    @Override
//    public ReviewLikes save(ReviewLikes reviewLikes) {
//        reviewLikes.setId(sequence++);
//        store.put(reviewLikes.getId(), reviewLikes);
//        return reviewLikes;
//    }
//
//    @Override
//    public List<ReviewLikes> findAll() {
//       return new ArrayList<>(store.values());
//    }
//
//    @Override
//    public Optional<ReviewLikes> findById(Long id) {
//        return Optional.ofNullable(store.get(id));
//    }
//
//    @Override
//    public List<ReviewLikes> findReviewLikesByUserId(Long userId) {
//        List<ReviewLikes> reviewLikes = new ArrayList<>();
//        for (ReviewLikes reviewLike : store.values()) {
//            if (reviewLike.getUserId().equals(userId)) {
//                reviewLikes.add(reviewLike);
//            }
//        }
//        return reviewLikes;
//    }
//
//    @Override
//    public void delete(Long userId, Long reviewId) {
//        for (ReviewLikes reviewLike : store.values()) {
//            if (reviewLike.getUserId().equals(userId) && reviewLike.getReviewId().equals(reviewId)) {
//                store.remove(reviewLike.getId());
//            }
//        }
//    }
//
//    @Override
//    public boolean findByUserIdAndReviewId(Long userId, Long reviewId) {
//        for (ReviewLikes reviewLike : store.values()) {
//            if (reviewLike.getUserId().equals(userId) && reviewLike.getReviewId().equals(reviewId)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean findByUserId(Long userId) {
//        for (ReviewLikes reviewLike : store.values()) {
//            if (reviewLike.getUserId().equals(userId)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void clearStore() {
//        store.clear();
//    }
//}